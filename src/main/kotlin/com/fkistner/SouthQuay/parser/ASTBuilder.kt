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
    })
}
