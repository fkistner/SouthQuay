package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*

sealed class ASTNode
data class Program(val statements: List<Statement> = listOf()) : ASTNode()

sealed class Statement : ASTNode()

object ASTBuilder : SQLangBaseVisitor<ASTNode>() {
    override fun visitProgram(ctx: SQLangParser.ProgramContext?): ASTNode {
        return Program()
    }
}

fun SQLangParser.toAST() : ASTNode {
    return this.program().accept(ASTBuilder)
}
