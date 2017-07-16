package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.SQLangParser


fun SQLangParser.toAST(errorContainer: MutableList<SQLangError> = mutableListOf()) = this.program().toAST(errorContainer)

fun SQLangParser.ProgramContext.toAST(errorContainer: MutableList<SQLangError> = mutableListOf()): Program {
    val scope = Scope(errorContainer)
    return Program(statement().map { it.toAST(scope) })
}

fun SQLangParser.StatementContext.toAST(scope: Scope) = accept(StatementASTBuilder(scope))

fun SQLangParser.ExpressionContext.toAST(scope: Scope): Expression = this.accept(ExpressionASTBuilder(scope))
