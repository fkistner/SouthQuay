package com.fkistner.SouthQuay.parser

sealed class SQLangError(val message: String?, val span: Span)

class SyntaxError(message: String?, val offendingSymbol: Any?, span: Span): SQLangError(message, span) {
    override fun toString(): String {
        return "SyntaxError(message='$message', offendingSymbol=$offendingSymbol, span=$span)"
    }
}

class TypeError(message: String, val offendingNode: ASTNode): SQLangError(message, offendingNode.span) {
    override fun toString(): String {
        return "TypeError(message='$message', offendingNode=$offendingNode)"
    }
}

class RuntimeError(message: String, val offendingNode: ASTNode): SQLangError(message, offendingNode.span) {
    override fun toString(): String {
        return "RuntimeError(message='$message', offendingNode=$offendingNode)"
    }
}
