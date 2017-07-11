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
private data class N(val context: String, var children: List<TestTree> = listOf()) : TestTree()
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

/** Assert that parser was able to parse source without errors */
private fun assertNoParserSyntaxErrors(parser: SQLangParser) {
    Assert.assertEquals("Parser should not have syntax errors.", 0, parser.numberOfSyntaxErrors)
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
                return N(node.ruleContext.javaClass.simpleName.removeSuffix("Context"), children)
            }
        })
    }

    @Test
    fun emptyInput() {
        val parser = parserForString("")

        val program = parser.program()

        Assert.assertEquals(N("Program", listOf(EOF)), toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun whitespaceInput() {
        val parser = parserForString("  \n  ")

        val program = parser.program()

        Assert.assertEquals(N("Program", listOf(EOF)), toTestTree(program))
        assertNoParserSyntaxErrors(parser)
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
                N("Program", listOf(
                        N("Statement", listOf(
                                N("Print", listOf(L("print"), L("\"hello\"")))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun printStatements() {
        val parser = parserForString("print \"hello\" print \"xyz\"")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Statement", listOf(
                                N("Print", listOf(L("print"), L("\"hello\"")))
                        )),
                        N("Statement", listOf(
                                N("Print", listOf(L("print"), L("\"xyz\"")))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
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

    @Test
    fun outputInteger() {
        val parser = parserForString("out 10")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Statement", listOf(
                                N("Out", listOf(
                                        L("out"),
                                        N("Number", listOf(L("10")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputNegativeInteger() {
        val parser = parserForString("out -42")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Statement", listOf(
                                N("Out", listOf(
                                        L("out"),
                                        N("Number", listOf(L("-"), L("42")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputDecimal() {
        val parser = parserForString("out 10.25")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Statement", listOf(
                                N("Out", listOf(
                                        L("out"),
                                        N("Number", listOf(L("10.25")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputNegativeDecimal() {
        val parser = parserForString("out -4321423.43245321459")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Statement", listOf(
                                N("Out", listOf(
                                        L("out"),
                                        N("Number", listOf(L("-"), L("4321423.43245321459")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputAddition() {
        val parser = parserForString("out 4+7.0")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Statement", listOf(
                                N("Out", listOf(
                                        L("out"),
                                        N("Sum", listOf(
                                                N("Number", listOf(L("4"))),
                                                L("+"),
                                                N("Number", listOf(L("7.0")))
                                        ))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputMultiAddition() {
        val parser = parserForString("out 4+7.0+2")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Statement", listOf(
                                N("Out", listOf(
                                        L("out"),
                                        N("Sum", listOf(
                                                N("Sum", listOf(
                                                        N("Number", listOf(L("4"))),
                                                        L("+"),
                                                        N("Number", listOf(L("7.0")))
                                                )),
                                                L("+"),
                                                N("Number", listOf(L("2")))
                                        ))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }
}
