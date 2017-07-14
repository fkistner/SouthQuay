package com.fkistner.SouthQuay.parser

sealed class SQLangError(val message: String?, val line: Int, val column: Int)

class SyntaxError(message: String?, val offendingSymbol: Any?, line: Int, column: Int): SQLangError(message, line, column) {
    override fun toString(): String {
        return "SyntaxError(message='$message', offendingSymbol=$offendingSymbol, line=$line, column=$column)"
    }
}

class TypeError(message: String, val offendingNode: ASTNode): SQLangError(message, 0, 0) {
    override fun toString(): String {
        return "TypeError(message='$message', offendingNode=$offendingNode)"
    }
}
