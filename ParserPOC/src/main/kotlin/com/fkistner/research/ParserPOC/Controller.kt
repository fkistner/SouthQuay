package com.fkistner.research.ParserPOC

/**
 * Created by florian on 7/7/17.
 */

import com.fkistner.research.ParserPOC.*;
import org.antlr.v4.runtime.*
import java.io.StringReader

fun main(args : Array<String>) {
    // CharStreams.fromString may cause crashes, see also https://github.com/antlr/antlr4/issues/1879
    val charStream = CharStreams.fromReader(StringReader("""
        print "a" print "b" print " "
        print ""
    """))

    println("Lexer:")
    val lexer = TestLangLexer(charStream)
    lexer.addErrorListener(object : BaseErrorListener() {
        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            e?.printStackTrace()
        }
    })

    try {
        do {
            val token = lexer.nextToken()
            if (token.type < 1) break
            println("${lexer.ruleNames[token.type - 1]} '${token.text}'")
        } while (true)
    } catch (e: Exception) {
        e.printStackTrace();
    }
    finally {
        lexer.reset()
    }

    println("Parser:")
    val parser = TestLangParser(CommonTokenStream(lexer))
    parser.addErrorListener(object : BaseErrorListener() {
        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {

        }
    })

    val program = parser.program()
    program.accept(object : TestLangBaseVisitor<Unit>() {
        override fun visitPrint(ctx: TestLangParser.PrintContext?) {
            println(ctx?.toStringTree())
        }
    })
    println("Errors: ${parser.numberOfSyntaxErrors}")
}