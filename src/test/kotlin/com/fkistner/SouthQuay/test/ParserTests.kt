package com.fkistner.SouthQuay.test

import org.junit.*
import com.fkistner.SouthQuay.grammar.*
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.TerminalNode
import java.io.StringReader

class ParserTests {

    object BailErrorListener: BaseErrorListener() {
        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            throw e ?: AssertionError("Recovered mismatched token error")
        }
    }

    private fun bailingParserForString(testString: String): SQLangParser {
        val charStream = CharStreams.fromReader(StringReader(testString))

        val lexer = SQLangLexer(charStream)
        lexer.addErrorListener(BailErrorListener)
        val parser = SQLangParser(CommonTokenStream(lexer))
        parser.addErrorListener(BailErrorListener)

        return parser
    }

    @Test
    fun emptyInput() {
        // Arrange
        val parser = bailingParserForString("")

        // Act
        val program = parser.program()

        // Assert = no exception
        Assert.assertEquals(1, program.childCount)
        Assert.assertEquals(Token.EOF, (program.getChild(0) as? TerminalNode)?.symbol?.type)
    }

    @Test
    fun whitespaceInput() {
        // Arrange
        val parser = bailingParserForString("  \n  ")

        // Act
        val program = parser.program()

        // Assert = no exception
        Assert.assertEquals(1, program.childCount)
        Assert.assertEquals(Token.EOF, (program.getChild(0) as? TerminalNode)?.symbol?.type)
    }

    @Test(expected = RecognitionException::class)
    fun triviallyIllegalInput() {
        // Arrange
        val parser = bailingParserForString("xyz")

        // Act
        parser.program()

        // Assert = RecognitionException
    }

}
