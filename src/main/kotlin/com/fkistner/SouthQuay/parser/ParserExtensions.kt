package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.SQLangParser
import org.antlr.v4.runtime.*


internal fun SQLangParser.ProgramContext.toAST(errorContainer: MutableList<SQLangError> = mutableListOf()): Program {
    val scope = Scope(errorContainer)
    return Program(statement().map { it.toAST(scope) }, scope).also { it.span = toSpan() }
}

internal fun SQLangParser.StatementContext .toAST(scope: Scope): Statement  = accept(StatementASTBuilder(scope))
internal fun SQLangParser.ExpressionContext.toAST(scope: Scope): Expression = accept(ExpressionASTBuilder(scope))

fun Program.verify() = acceptChildren(ASTVerifier(scope))
fun Lambda .verify() = acceptChildren(ASTVerifier(scope))

fun Token.toSpan() = this.to(this).toSpan()

fun ParserRuleContext.toSpan(): Span = start.to(stop).toSpan()

fun Pair<Token, Token>.toSpan(): Span {
    val (startToken, stopToken) = this
    val start = Position(startToken.line, startToken.charPositionInLine, startToken.startIndex)
    val stop = Position(stopToken.line, stopToken.charPositionInLine + stopToken.stopIndex - stopToken.startIndex + 1, stopToken.stopIndex + 1)
    return Span(start, stop)
}
