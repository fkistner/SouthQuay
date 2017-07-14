package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*
import org.antlr.v4.runtime.*

interface ASTVisitor {
    fun visit(variableRef: VariableRef) = true
    fun visit(lambda: Lambda) = true
    fun visit(functionInvoc: FunctionInvoc) = true
    fun visit(sum: Sum) = true
    fun visit(sub: Sub) = true
    fun visit(mul: Mul) = true
    fun visit(div: Div) = true
    fun visit(pow: Pow) = true
    fun visit(sequence: Sequence) = true
    fun visit(realLiteral: RealLiteral) = true
    fun visit(integerLiteral: IntegerLiteral) = true
    fun visit(varStatement: VarStatement) = true
    fun visit(outStatement: OutStatement) = true
    fun visit(printStatement: PrintStatement) = true
    fun visit(program: Program) = true
    fun endVisit(variableRef: VariableRef) = Unit
    fun endVisit(lambda: Lambda) = Unit
    fun endVisit(functionInvoc: FunctionInvoc) = Unit
    fun endVisit(sum: Sum) = Unit
    fun endVisit(sub: Sub) = Unit
    fun endVisit(mul: Mul) = Unit
    fun endVisit(div: Div) = Unit
    fun endVisit(pow: Pow) = Unit
    fun endVisit(sequence: Sequence) = Unit
    fun endVisit(realLiteral: RealLiteral) = Unit
    fun endVisit(integerLiteral: IntegerLiteral) = Unit
    fun endVisit(varStatement: VarStatement) = Unit
    fun endVisit(outStatement: OutStatement) = Unit
    fun endVisit(printStatement: PrintStatement) = Unit
    fun endVisit(program: Program) = Unit
}

sealed class ASTNode {
    open val children: List<ASTNode> = emptyList()
    fun accept(visitor: ASTVisitor) {
        if (visit(visitor)) children.map { it.accept(visitor) }
        endVisit(visitor)
    }
    protected abstract fun    visit(visitor: ASTVisitor): Boolean
    protected abstract fun endVisit(visitor: ASTVisitor)
}

data class Program(val statements: List<Statement> = listOf()): ASTNode() {
    override val children get() = statements
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

sealed class Statement : ASTNode()
data class PrintStatement(val stringLiteral: String) : Statement() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class OutStatement  (val expression: Expression): Statement() {
    override val children get() = listOf(expression)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class VarStatement  (val identifier: String, val expression: Expression): Statement() {
    override val children get() = listOf(expression)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

sealed class Expression : ASTNode()
data class IntegerLiteral(val value: Int)   : Expression() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class RealLiteral   (val value: Double): Expression() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Sequence      (val from: Expression, val to: Expression): Expression() {
    override val children get() = listOf(from, to)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

sealed class BinaryOperation: Expression() {
    abstract val left: Expression
    abstract val right: Expression
    override val children get() = listOf(left, right)
}

data class Sum(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Sub(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Mul(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Div(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Pow(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

data class FunctionInvoc(val identifier: String, val args: List<Expression>): Expression(){
    override val children get() = args
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Lambda(val parameters: List<String>, val body: Expression): Expression(){
    override val children get() = listOf(body)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class VariableRef(val identifier: String): Expression() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}


fun SQLangParser.toAST() = this.program().toAST()

fun SQLangParser.ProgramContext.toAST() = Program(statement().map { it.toAST() })

fun SQLangParser.StatementContext.toAST(): Statement {
    return this.accept(object : SQLangBaseVisitor<Statement>() {
        override fun visitPrint(ctx: SQLangParser.PrintContext): Statement {
            return PrintStatement(ctx.string.text.trim('"'))
        }

        override fun visitOut(ctx: SQLangParser.OutContext): Statement {
            return OutStatement(ctx.expr.toAST())
        }

        override fun visitVar(ctx: SQLangParser.VarContext): Statement {
            return VarStatement(ctx.ident.text, ctx.expr.toAST())
        }
    })
}

fun SQLangParser.ExpressionContext.toAST(): Expression {
    return this.accept(object : SQLangBaseVisitor<Expression>() {
        override fun visitNumber(ctx: SQLangParser.NumberContext): Expression {
            ctx.integer?.let {
                val value = it.text.toInt()
                return IntegerLiteral(if (ctx.minus == null) value else value.unaryMinus())
            }
            ctx.real.let {
                val value = it.text.toDouble()
                return RealLiteral(if (ctx.minus == null) value else value.unaryMinus())
            }
        }

        override fun visitSeq(ctx: SQLangParser.SeqContext): Expression {
            return Sequence(ctx.from.toAST(), ctx.to.toAST())
        }

        override fun visitMul(ctx: SQLangParser.MulContext): Expression {
            val left = ctx.left.toAST()
            val right = ctx.right.toAST()
            return if (ctx.op.type == SQLangParser.MUL) Mul(left, right) else Div(left, right)
        }

        override fun visitPow(ctx: SQLangParser.PowContext): Expression {
            return Pow(ctx.left.toAST(), ctx.right.toAST())
        }

        override fun visitSum(ctx: SQLangParser.SumContext): Expression {
            val left = ctx.left.toAST()
            val right = ctx.right.toAST()
            return if (ctx.op.type == SQLangParser.PLUS) Sum(left, right) else Sub(left, right)
        }

        override fun visitParen(ctx: SQLangParser.ParenContext): Expression {
            return ctx.expr.toAST();
        }

        override fun visitRef(ctx: SQLangParser.RefContext): Expression {
            return VariableRef(ctx.ident.text)
        }

        override fun visitLam(ctx: SQLangParser.LamContext): Expression {
            return Lambda(ctx.params.map { it.text }, ctx.body.toAST())
        }

        override fun visitFun(ctx: SQLangParser.FunContext): Expression {
            return FunctionInvoc(ctx.`fun`.text, ctx.arg.map { it.toAST() })
        }
    })
}

object ASTBuilder {
    fun parseStream(charStream: CharStream, errorContainer: MutableList<SQLangError> = mutableListOf()): Program? {
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
        return program.toAST()
    }
}