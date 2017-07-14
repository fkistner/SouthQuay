package com.fkistner.SouthQuay.grammar

import com.fkistner.SouthQuay.grammar.*
import org.antlr.v4.runtime.*
import java.io.StringReader

internal fun parserForString(testString: String): SQLangParser {
    val charStream = CharStreams.fromReader(StringReader(testString))
    val lexer = SQLangLexer(charStream)
    val parser = SQLangParser(CommonTokenStream(lexer))
    return parser
}