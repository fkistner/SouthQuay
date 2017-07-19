package com.fkistner.SouthQuay.parser

import org.antlr.v4.runtime.Token

/**
 * Super class for parsing, type, and runtime errors.
 * @param message Error message
 * @param span Span in source the error originates from
 */
sealed class SQLangError(val message: String?, val span: Span)

/**
 * Error occurred during parsing of the program text.
 *
 * @param message Error message
 * @param offendingToken Token that triggered error
 * @param span Source span that human-friendly describes the error
 */
class SyntaxError(message: String?, val offendingToken: Token, span: Span): SQLangError(message, span) {
    override fun toString(): String {
        return "SyntaxError(message='$message', offendingToken=$offendingToken, span=$span)"
    }
}

/**
 * Error occurred during the AST construction or AST verification of the program.
 *
 * @param message Error message
 * @param offendingNode Node that failed AST construction or AST verification
 */
class TypeError(message: String, val offendingNode: ASTNode): SQLangError(message, offendingNode.span) {
    override fun toString(): String {
        return "TypeError(message='$message', offendingNode=$offendingNode)"
    }
}

/**
 * Error occurred during the Runtime of the program.
 *
 * @param throwable Error occurred during execution
 * @param offendingNode Node during which execution an exception was triggered
 */
class RuntimeError(throwable: Throwable, val offendingNode: ASTNode): SQLangError(throwable.localizedMessage, offendingNode.span) {
    override fun toString(): String {
        return "RuntimeError(message='$message', offendingNode=$offendingNode)"
    }
}
