package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.SQLangParser
import org.antlr.v4.runtime.*

/**
 * Traverses this rule and all child rules for parsing exceptions.
 * @return First exception encountered, or `null` if the parse tree is well formed.
 */
internal fun ParserRuleContext.getExceptionsRecursive(): RecognitionException? {
    return exception ?: children.asSequence()
            .mapNotNull { it as? ParserRuleContext }
            .mapNotNull { it.getExceptionsRecursive() }
            .firstOrNull()
}

/**
 * Translates the program context into the program abstract syntax tree.
 * @param errorContainer Container for recording errors
 * @return The complete abstract syntax tree, if the parse tree is well formed,
 *         or the partial abstract syntax tree recovered from the source
 */
internal fun SQLangParser.ProgramContext.toAST(errorContainer: MutableList<SQLangError> = mutableListOf()): Program {
    val scope = Scope(errorContainer)
    return Program(statement().asSequence()
            .filter { it.getExceptionsRecursive() == null }
            .map { it.toAST(scope) }
            .toList(), scope).also { it.span = toSpan() }
}

/**
 * Translates the statement context using the [StatementASTBuilder].
 * @param scope Current visibility scope
 * @return The statement abstract syntax tree
 */
internal fun SQLangParser.StatementContext .toAST(scope: Scope): Statement  = accept(StatementASTBuilder(scope))

/**
 * Translates the expression context using the [ExpressionASTBuilder].
 * @return The expression abstract syntax tree
 * @param scope Current visibility scope
 */
internal fun SQLangParser.ExpressionContext.toAST(scope: Scope): Expression = accept(ExpressionASTBuilder(scope))

/** Verifies the program abstract syntax tree using the [ASTVerifier]. */
fun Program.verify() = acceptChildren(ASTVerifier(scope))
/** Verifies the lambda abstract syntax tree using the [ASTVerifier]. */
fun Lambda .verify() = acceptChildren(ASTVerifier(scope))

/**
 * Extracts the text span from the token.
 * @return Span of the token in source
 */
fun Token.toSpan() = this.to(this).toSpan()

/**
 * Extracts the text span from the parser rule context.
 * @return Span of the parsed rule in source
 */
fun ParserRuleContext.toSpan(): Span = start.to(stop).toSpan()

/**
 * Extracts the text span between a pair of tokens.
 * @return Span between the tokens in source
 */
fun Pair<Token, Token>.toSpan(): Span {
    val (startToken, stopToken) = this
    val start = Position(startToken.line, startToken.charPositionInLine, startToken.startIndex)
    val stop = Position(stopToken.line, stopToken.charPositionInLine + stopToken.stopIndex - stopToken.startIndex + 1, stopToken.stopIndex + 1)
    return Span(start, stop)
}
