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

/** Assert that parser has detected syntax errors */
private fun assertParserSyntaxErrors(parser: SQLangParser) {
    Assert.assertTrue("Parser should have syntax errors.", parser.numberOfSyntaxErrors > 0)
}

class ParserTests {

    private fun parserForString(testString: String): SQLangParser {
        val charStream = CharStreams.fromReader(StringReader(testString))
        val lexer = SQLangLexer(charStream)
        val parser = SQLangParser(CommonTokenStream(lexer))
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
        val parser = parserForString("")

        val program = parser.program()

        Assert.assertEquals(N(RULE_program, listOf(EOF)), toTestTree(program))
    }

    @Test
    fun whitespaceInput() {
        val parser = parserForString("  \n  ")

        val program = parser.program()

        Assert.assertEquals(N(RULE_program, listOf(EOF)), toTestTree(program))
    }

    @Test
    fun triviallyIllegalInput() {
        val parser = parserForString("xyz")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

    @Test
    fun printStatement() {
        val parser = parserForString("print \"hello\"")

        val program = parser.program()

        Assert.assertEquals(
                N(RULE_program, listOf(
                        N(RULE_statement, listOf(
                                N(RULE_print, listOf(L("print"), L("\"hello\"")))
                        )),
                        EOF
                )),
                toTestTree(program))
    }

    @Test
    fun printStatements() {
        val parser = parserForString("print \"hello\" print \"xyz\"")

        val program = parser.program()

        Assert.assertEquals(
                N(RULE_program, listOf(
                        N(RULE_statement, listOf(
                                N(RULE_print, listOf(L("print"), L("\"hello\"")))
                        )),
                        N(RULE_statement, listOf(
                                N(RULE_print, listOf(L("print"), L("\"xyz\"")))
                        )),
                        EOF
                )),
                toTestTree(program))
    }

    @Test
    fun printUnbalancedQuotesA() {
        val parser = parserForString("print \"hello print \"xyz\"")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

    @Test
    fun printUnbalancedQuotesB() {
        val parser = parserForString("print \"hello\" print \"xyz")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

    @Test
    fun printUnbalancedQuotesC() {
        val parser = parserForString("print \"hello\" print xyz\"")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

}
