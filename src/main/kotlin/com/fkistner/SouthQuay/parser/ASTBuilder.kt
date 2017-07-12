package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*

sealed class ASTNode
data class Program(val statements: List<Statement> = listOf()) : ASTNode()

sealed class Statement : ASTNode()
data class PrintStatement(val stringLiteral: String)  : Statement()
data class OutStatement  (val expression: Expression) : Statement()
data class VarStatement  (val identifier: String, val expression: Expression) : Statement()

sealed class Expression : ASTNode()
data class IntegerLiteral(val value: Int)    : Expression()
data class RealLiteral   (val value: Double) : Expression()
data class Sequence      (val from: Expression, val to: Expression) : Expression()

data class Sum(val left: Expression, val right: Expression) : Expression()
data class Sub(val left: Expression, val right: Expression) : Expression()
data class Mul(val left: Expression, val right: Expression) : Expression()
data class Div(val left: Expression, val right: Expression) : Expression()
data class Pow(val left: Expression, val right: Expression) : Expression()

data class FunctionInvoc(val identifier: String, val args: List<Expression>) : Expression()
data class Lambda(val parameters: List<String>, val body: Expression) : Expression()
data class VariableRef(val identifier: String) : Expression()


fun SQLangParser.toAST() : Program {
    return this.program().toAST()
}

fun SQLangParser.ProgramContext.toAST() : Program {
    val children = this.statement().map { it.accept(object : SQLangBaseVisitor<Statement>() {
        override fun visitPrint(ctx: SQLangParser.PrintContext): Statement {
            return PrintStatement(ctx.string.text.trim('"'))
        }

        override fun visitOut(ctx: SQLangParser.OutContext): Statement {
            return OutStatement(ctx.expr.toAST())
        }

        override fun visitVar(ctx: SQLangParser.VarContext): Statement {
            return VarStatement(ctx.ident.text, ctx.expr.toAST())
        }
    }) }
    return Program(children)
}

fun SQLangParser.ExpressionContext.toAST() : Expression {
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
