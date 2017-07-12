package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*

sealed class ASTNode
data class Program(val statements: List<Statement> = listOf()) : ASTNode()

sealed class Statement : ASTNode()
data class PrintStatement(val stringLiteral: String) : Statement()
data class OutStatement(val expression: Expression) : Statement()

sealed class Expression : ASTNode()
data class IntegerLiteral(val value: Int) : Expression()

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
    }) }
    return Program(children)
}

fun SQLangParser.ExpressionContext.toAST() : Expression {
    return this.accept(object : SQLangBaseVisitor<Expression>() {
        override fun visitNumber(ctx: SQLangParser.NumberContext): Expression {
            val int = ctx.Integer().text.toInt()
            return IntegerLiteral(if (ctx.MINUS() == null) int else int.unaryMinus())
        }
    })
}
