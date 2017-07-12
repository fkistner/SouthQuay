package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*

sealed class ASTNode
data class Program(val statements: List<Statement> = listOf()) : ASTNode()

sealed class Statement : ASTNode()
data class PrintStatement(val stringLiteral: String) : Statement()

fun SQLangParser.toAST() : Program {
    return this.program().toAST()
}

fun SQLangParser.ProgramContext.toAST() : Program {
    val children = this.statement().map { it.accept(object : SQLangBaseVisitor<Statement>() {
        override fun visitPrint(ctx: SQLangParser.PrintContext): Statement {
            return PrintStatement(ctx.String().text.trim('"'))
        }
    }) }
    return Program(children)
}
