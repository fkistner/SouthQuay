package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*

/**
 * Recursively translates expression parser rules to [Expression]s by visiting the parse tree.
 * @param scope Current visibility scope
 */
class ExpressionASTBuilder(val scope: Scope): SQLangBaseVisitor<Expression>() {
    fun SQLangParser.ExpressionContext.toAST(): Expression = accept(this@ExpressionASTBuilder)

    override fun visitNumber(ctx: SQLangParser.NumberContext): Expression {
        ctx.integer?.let {
            return try {
                val value = it.text.toInt()
                IntegerLiteral(if (ctx.minus == null) value else value.unaryMinus()).also { it.span = ctx.toSpan() }
            } catch (e: NumberFormatException) {
                val literal = IntegerLiteral(Int.MIN_VALUE).also { it.span = ctx.toSpan() }
                scope.errorContainer.add(TypeError("Number is not within value range [${Int.MIN_VALUE}, ${Int.MAX_VALUE}]", literal))
                literal
            }
        }
        ctx.real.let {
            return try {
                val value = it.text.toDouble()
                RealLiteral(if (ctx.minus == null) value else value.unaryMinus()).also { it.span = ctx.toSpan() }
            } catch (e: NumberFormatException) {
                val literal = RealLiteral(Double.NaN).also { it.span = ctx.toSpan() }
                scope.errorContainer.add(TypeError("Number is not within value range [${Double.MIN_VALUE}, ${Double.MAX_VALUE}]", literal))
                literal
            }
        }
    }

    override fun visitSeq(ctx: SQLangParser.SeqContext): Sequence {
        val from = ctx.from.toAST()
        val to = ctx.to.toAST()
        return Sequence(from, to).also { it.span = ctx.toSpan() }
    }

    override fun visitMul(ctx: SQLangParser.MulContext): BinaryOperation {
        val left = ctx.left.toAST()
        val right = ctx.right.toAST()
        return (if (ctx.op.type == SQLangParser.MUL) Mul(left, right) else Div(left, right)).also { it.span = ctx.toSpan() }
    }

    override fun visitSum(ctx: SQLangParser.SumContext): BinaryOperation {
        val left = ctx.left.toAST()
        val right = ctx.right.toAST()
        return (if (ctx.op.type == SQLangParser.PLUS) Sum(left, right) else Sub(left, right)).also { it.span = ctx.toSpan() }
    }

    override fun visitPow(ctx: SQLangParser.PowContext) = Pow(ctx.left.toAST(), ctx.right.toAST())
            .also { it.span = ctx.toSpan() }

    override fun visitParen(ctx: SQLangParser.ParenContext): Expression {
        return ctx.expr.toAST().also { it.span = ctx.toSpan() }
    }

    override fun visitRef(ctx: SQLangParser.RefContext): VariableRef {
        val identifier = ctx.ident.text
        val declaration = scope.variables[identifier]
        val variableRef = VariableRef(identifier, declaration).also { it.span = ctx.toSpan() }
        if (declaration == null) scope.errorContainer.add(TypeError("Unknown variable '$identifier'", variableRef))
        return variableRef
    }

    override fun visitLam(ctx: SQLangParser.LamContext): Lambda {
        val lambdaScope = Scope(scope)
        val parameters = ctx.params.map { param -> VarDeclaration(param.text, Type.Error).also { it.span = param.toSpan() } }
        parameters.map { lambdaScope.variables[it.identifier] = it }
        return Lambda(parameters, ctx.body.toAST(lambdaScope), lambdaScope).also { it.span = ctx.toSpan() }
    }

    override fun visitFun(ctx: SQLangParser.FunContext): FunctionInvoc {
        val args = ctx.arg.map { it.toAST(scope) }

        val signature = FunctionSignature(ctx.`fun`.text, ctx.arg.size) //args.map { it.type })
        val target = scope.functions[signature]
        val functionInvoc = FunctionInvoc(signature.identifier, args, target).also { it.span = ctx.toSpan() }
        if (target == null) scope.errorContainer.add(TypeError("Function ${signature.identifier}(" +
                "${(1..signature.argumentCount).map { "…" }.joinToString()}) is not defined", functionInvoc))
        return functionInvoc
    }
}
