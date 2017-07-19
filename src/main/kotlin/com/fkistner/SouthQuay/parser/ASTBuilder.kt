package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*
import org.antlr.v4.runtime.*
import java.io.StringReader


/** Provides a facade for the parsing of the program text into the abstract syntax tree. */
object ASTBuilder {
    /**
     * Parses the program and provides information about parsing errors.
     * @param charStream Program text as character stream
     * @param errorContainer Container encountered errors are added to
     * @return AST root with all statements that could be parsed
     */
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
                errorContainer.add(SyntaxError(msg, offendingToken, span))
            }
        }

        val lexer = SQLangLexer(charStream)
        lexer.addErrorListener(errorListener)
        val parser = SQLangParser(CommonTokenStream(lexer))
        parser.addErrorListener(errorListener)

        val program = parser.program()
        return program.toAST(errorContainer).also { it.verify() }
    }

    /**
     * Parses the program and provides information about parsing errors.
     * @param charStream Program text as character stream
     * @return AST root with all statements that could be parsed and list of encountered errors
     */
    fun parseStream(charStream: CharStream): Pair<Program?, MutableList<SQLangError>> {
        val errorContainer  = mutableListOf<SQLangError>()
        return Pair(parseStream(charStream, errorContainer), errorContainer)
    }

    /**
     * Parses the program and provides information about parsing errors.
     * @param text Program text as string
     * @return AST root with all statements that could be parsed and list of encountered errors
     */
    fun parseText(text: String): Pair<Program?, MutableList<SQLangError>> {
        val charStream = CharStreams.fromReader(StringReader(text))
        return parseStream(charStream)
    }
}