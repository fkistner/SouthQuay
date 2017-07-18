package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*
import org.antlr.v4.runtime.*
import java.io.StringReader


object ASTBuilder {
    fun parseStream(charStream: CharStream, errorContainer: MutableList<SQLangError>): Program? {
        val errorListener = object: BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*, *>, offendingSymbol: Any?, line: Int,
                                     charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                val offendingToken = offendingSymbol as Token
                var span = offendingToken.toSpan()
                if (span.length == 0 && recognizer is Parser) {
                    var context: ParserRuleContext? = recognizer.context
                    while (context != null && context.start == offendingToken) {
                        context = context.getParent()
                    }
                    context?.let { span = it.start.to(offendingToken).toSpan() }
                }
                errorContainer.add(SyntaxError(msg, offendingSymbol, span))
            }
        }

        val lexer = SQLangLexer(charStream)
        lexer.addErrorListener(errorListener)
        val parser = SQLangParser(CommonTokenStream(lexer))
        parser.addErrorListener(errorListener)

        val program = parser.program()
        return program.toAST(errorContainer).also { it.verify() }
    }

    fun parseStream(charStream: CharStream): Pair<Program?, MutableList<SQLangError>> {
        val errorContainer  = mutableListOf<SQLangError>()
        return Pair(parseStream(charStream, errorContainer), errorContainer)
    }

    fun parseText(text: String): Pair<Program?, MutableList<SQLangError>> {
        val charStream = CharStreams.fromReader(StringReader(text))
        return parseStream(charStream)
    }
}