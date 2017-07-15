package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*
import org.antlr.v4.runtime.*
import java.io.StringReader

fun SQLangParser.toAST(errorContainer: MutableList<SQLangError> = mutableListOf()) = this.program().toAST(errorContainer)

fun SQLangParser.ProgramContext.toAST(errorContainer: MutableList<SQLangError> = mutableListOf()): Program {
    val scope = Scope(errorContainer)
    return Program(statement().map { it.toAST(scope) })
}

fun SQLangParser.StatementContext.toAST(scope: Scope): Statement {
    return this.accept(object : SQLangBaseVisitor<Statement>() {
        override fun visitPrint(ctx: SQLangParser.PrintContext): Statement {
            return PrintStatement(ctx.string.text.trim('"'))
        }

        override fun visitOut(ctx: SQLangParser.OutContext): Statement {
            return OutStatement(ctx.expr.toAST(scope))
        }

        override fun visitVar(ctx: SQLangParser.VarContext): Statement {
            val identifier = ctx.ident.text
            val expression = ctx.expr.toAST(scope)
            val varDeclaration = VarDeclaration(identifier, expression.type)
            val varStatement = VarStatement(varDeclaration, expression)
            if (scope.variables.containsKey(identifier)) scope.errorContainer.add(TypeError("Variable '$identifier' redeclared", varStatement))
            scope.variables[identifier] = varDeclaration
            return varStatement
        }
    })
}

fun SQLangParser.ExpressionContext.toAST(scope: Scope): Expression {
    return this.accept(object : SQLangBaseVisitor<Expression>() {
        override fun visitNumber(ctx: SQLangParser.NumberContext): Expression {
            ctx.integer?.let {
                try {
                    val value = it.text.toInt()
                    return IntegerLiteral(if (ctx.minus == null) value else value.unaryMinus())
                } catch (e: NumberFormatException) {
                    val literal = IntegerLiteral(Int.MIN_VALUE)
                    scope.errorContainer.add(TypeError("Number is not within value range [${Int.MIN_VALUE}, ${Int.MAX_VALUE}]", literal))
                    return literal
                }
            }
            ctx.real.let {
                try {
                    val value = it.text.toDouble()
                    return RealLiteral(if (ctx.minus == null) value else value.unaryMinus())
                } catch (e: NumberFormatException) {
                    val literal = RealLiteral(Double.NaN)
                    scope.errorContainer.add(TypeError("Number is not within value range [${Double.MIN_VALUE}, ${Double.MAX_VALUE}]", literal))
                    return literal
                }
            }
        }

        fun checkBinaryOperation(op: BinaryOperation) {
            if (op.left.type == Type.Error || op.right.type == Type.Error) return
            if (op.type == Type.Error)
                scope.errorContainer.add(TypeError("Incompatible arguments ${op.left.type} and ${op.right.type}", op))
        }

        override fun visitSeq(ctx: SQLangParser.SeqContext): Expression {
            val from = ctx.from.toAST(scope)
            val to = ctx.to.toAST(scope)
            when (from.type) {
                Type.Error, Type.Integer -> {}
                else -> scope.errorContainer.add(TypeError("Illegal sequence start, expected Integer, found $from.type", from))
            }
            when (to.type) {
                Type.Error, Type.Integer -> {}
                else -> scope.errorContainer.add(TypeError("Illegal sequence end, expected Integer, found $to.type", to))
            }
            return Sequence(from, to)
        }

        override fun visitMul(ctx: SQLangParser.MulContext): Expression {
            val left = ctx.left.toAST(scope)
            val right = ctx.right.toAST(scope)
            val op = if (ctx.op.type == SQLangParser.MUL) Mul(left, right) else Div(left, right)
            checkBinaryOperation(op)
            return op
        }

        override fun visitPow(ctx: SQLangParser.PowContext): Expression {
            val pow = Pow(ctx.left.toAST(scope), ctx.right.toAST(scope))
            checkBinaryOperation(pow)
            return pow
        }

        override fun visitSum(ctx: SQLangParser.SumContext): Expression {
            val left = ctx.left.toAST(scope)
            val right = ctx.right.toAST(scope)
            val op = if (ctx.op.type == SQLangParser.PLUS) Sum(left, right) else Sub(left, right)
            checkBinaryOperation(op)
            return op
        }

        override fun visitParen(ctx: SQLangParser.ParenContext): Expression {
            return ctx.expr.toAST(scope)
        }

        override fun visitRef(ctx: SQLangParser.RefContext): Expression {
            val identifier = ctx.ident.text
            val declaration = scope.variables[identifier]
            val variableRef = VariableRef(identifier, declaration)
            if (declaration == null) scope.errorContainer.add(TypeError("Unknown variable '$identifier'", variableRef))
            return variableRef
        }

        override fun visitLam(ctx: SQLangParser.LamContext): Expression {
            val lambdaScope = Scope(scope)
            val parameters = ctx.params.map { VarDeclaration(it.text, Type.Integer) }
            parameters.map { lambdaScope.variables[it.identifier] = it }
            return Lambda(parameters, ctx.body.toAST(lambdaScope))
        }

        override fun visitFun(ctx: SQLangParser.FunContext): Expression {
            val args = ctx.arg.map { it.toAST(scope) }
            val signature = FunctionSignature(ctx.`fun`.text, args.map { it.type })
            val target = scope.functions[signature]
            val functionInvoc = FunctionInvoc(signature.identifier, args, target)
            if (target == null) scope.errorContainer.add(TypeError("Function ${signature.identifier}(${signature.argumentTypes.joinToString()}) is not defined", functionInvoc))
            return functionInvoc
        }
    })
}

object ASTBuilder {
    fun parseStream(charStream: CharStream, errorContainer: MutableList<SQLangError>): Program? {
        val errorListener = object : BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                errorContainer.add(SyntaxError(msg, offendingSymbol, line, charPositionInLine))
            }
        }

        val lexer = SQLangLexer(charStream)
        lexer.addErrorListener(errorListener)
        val parser = SQLangParser(CommonTokenStream(lexer))
        parser.addErrorListener(errorListener)

        val program = parser.program()

        if (errorContainer.count() > 0) {
            return null
        }
        return program.toAST(errorContainer)
    }

    fun parseStream(charStream: CharStream): Pair<Program?, MutableList<SQLangError>> {
        val errorContainer  = mutableListOf<SQLangError>()
        return Pair(parseStream(charStream, errorContainer), errorContainer)
    }

    fun parseText(text: String): Pair<Program?, MutableList<SQLangError>> {
        val charStream = CharStreams.fromReader(StringReader(text))
        return parseStream(charStream)
    }
}