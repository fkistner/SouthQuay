package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.grammar.*
import com.fkistner.SouthQuay.grammar.SQLangParser.*
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import org.junit.*
import java.io.StringReader

/** Simplified tree of parsed rules and tokens */
private sealed class TestTree
/** Rule Node */
private data class N(val ruleIdx: Int, var children: List<TestTree> = listOf()) : TestTree()
/** Leaf Node */
private data class L(val text: String) : TestTree()
/** Error Node */
private object Error : TestTree()
/** EOF Node */
private object EOF : TestTree()

class ParserTests {

    object BailErrorListener: BaseErrorListener() {
        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            throw e ?: AssertionError("Recovered mismatched token error")
        }
    }

    private fun parserForString(testString: String): Pair<SQLangLexer, SQLangParser> {
        val charStream = CharStreams.fromReader(StringReader(testString))
        val lexer = SQLangLexer(charStream)
        val parser = SQLangParser(CommonTokenStream(lexer))
        return Pair(lexer, parser)
    }

    private fun bailingParserForString(testString: String): SQLangParser {
        val (lexer, parser) = parserForString(testString)
        lexer.addErrorListener(BailErrorListener)
        parser.addErrorListener(BailErrorListener)
        return parser
    }

    private fun toTestTree(program: ProgramContext) : TestTree {
        return program.accept(object : SQLangBaseVisitor<TestTree>() {
            override fun visitTerminal(node: TerminalNode): TestTree {
                return if (node.symbol.type == Recognizer.EOF)
                    EOF
                else
                    L(node.text)
            }

            override fun visitErrorNode(node: ErrorNode): TestTree {
                return Error
            }

            override fun visitChildren(node: RuleNode): TestTree {
                val children = (0 until node.childCount).map { node.getChild(it).accept(this) }
                return N(node.ruleContext.ruleIndex, children)
            }
        })
    }

    @Test
    fun emptyInput() {
        // Arrange
        val parser = bailingParserForString("")

        // Act
        val program = parser.program()

        // Assert = no exception
        Assert.assertEquals(N(RULE_program, listOf(EOF)), toTestTree(program))
    }

    @Test
    fun whitespaceInput() {
        // Arrange
        val parser = bailingParserForString("  \n  ")

        // Act
        val program = parser.program()

        // Assert = no exception
        Assert.assertEquals(N(RULE_program, listOf(EOF)), toTestTree(program))
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
