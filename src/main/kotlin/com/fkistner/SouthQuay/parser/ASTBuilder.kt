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
            return PrintStatement(ctx.String().text.trim('"'))
        }

        override fun visitOut(ctx: SQLangParser.OutContext): Statement {
            val expression = ctx.expression().toAST()
            return OutStatement(expression)
        }

        override fun visitVar(ctx: SQLangParser.VarContext): Statement {
            val expression = ctx.expression().toAST()
            return VarStatement(ctx.Identifier().text, expression)
        }
    }) }
    return Program(children)
}

fun SQLangParser.ExpressionContext.toAST() : Expression {
    return this.accept(object : SQLangBaseVisitor<Expression>() {
        override fun visitNumber(ctx: SQLangParser.NumberContext): Expression {
            val minus = ctx.MINUS()
            ctx.Integer()?.let {
                val value = it.text.toInt()
                return IntegerLiteral(if (minus == null) value else value.unaryMinus())
            }
            ctx.Real().let {
                val value = it.text.toDouble()
                return RealLiteral(if (minus == null) value else value.unaryMinus())
            }
        }

        override fun visitSeq(ctx: SQLangParser.SeqContext): Expression {
            return Sequence(ctx.from.toAST(), ctx.to.toAST())
        }
    })
}
