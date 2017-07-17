package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.SQLangParser
import org.antlr.v4.runtime.*

fun SQLangParser.toAST(errorContainer: MutableList<SQLangError> = mutableListOf()) = this.program().toAST(errorContainer)

fun SQLangParser.ProgramContext.toAST(errorContainer: MutableList<SQLangError> = mutableListOf()): Program {
    val scope = Scope(errorContainer)
    return Program(statement().map { it.toAST(scope) }).also { it.span = toSpan() }
}

fun SQLangParser.StatementContext.toAST(scope: Scope) = accept(StatementASTBuilder(scope))

fun SQLangParser.ExpressionContext.toAST(scope: Scope): Expression = this.accept(ExpressionASTBuilder(scope))

fun Token.toSpan() = this.to(this).toSpan()

fun ParserRuleContext.toSpan(): Span = start.to(stop).toSpan()

fun Pair<Token, Token>.toSpan(): Span {
    val (startToken, stopToken) = this
    val start = Position(startToken.line, startToken.charPositionInLine, startToken.startIndex)
    val stop = Position(stopToken.line, stopToken.charPositionInLine + stopToken.stopIndex - stopToken.startIndex + 1, stopToken.stopIndex + 1)
    val span = Span(start, stop)
    return span
}
