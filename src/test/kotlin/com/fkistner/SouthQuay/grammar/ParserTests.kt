package com.fkistner.SouthQuay.grammar

import com.fkistner.SouthQuay.grammar.SQLangParser.ProgramContext
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.tree.*
import org.junit.*

class ParserTests {

    private fun toTestTree(program: ProgramContext): TestTree {
        return program.accept(object : SQLangBaseVisitor<TestTree>() {
            override fun visitTerminal(node: TerminalNode): TestTree {
                return if (node.symbol.type == Recognizer.EOF)
                    EOF
                else
                    L(node.text)
            }

            override fun visitErrorNode(node: ErrorNode): TestTree {
                return Error(node.text)
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
                        N("Print", listOf(L("print"), L("\"hello\""))),
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
                        N("Print", listOf(L("print"), L("\"hello\""))),
                        N("Print", listOf(L("print"), L("\"xyz\""))),
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
                        N("Out", listOf(
                                L("out"),
                                N("Number", listOf(L("10")))
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
                        N("Out", listOf(
                                L("out"),
                                N("Number", listOf(L("-"), L("42")))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputReal() {
        val parser = parserForString("out 10.25")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Number", listOf(L("10.25")))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputBadReal() {
        val parser = parserForString("out 10.25.123")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

    @Test
    fun outputNegativeReal() {
        val parser = parserForString("out -4321423.43245321459")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Number", listOf(L("-"), L("4321423.43245321459")))
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
                        N("Out", listOf(
                                L("out"),
                                N("Sum", listOf(
                                        N("Number", listOf(L("4"))),
                                        L("+"),
                                        N("Number", listOf(L("7.0")))
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
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputSubtraction() {
        val parser = parserForString("out 542-1342")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Sum", listOf(
                                        N("Number", listOf(L("542"))),
                                        L("-"),
                                        N("Number", listOf(L("1342")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputMultiSubtraction() {
        val parser = parserForString("out 533+42-1342")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Sum", listOf(
                                        N("Sum", listOf(
                                                N("Number", listOf(L("533"))),
                                                L("+"),
                                                N("Number", listOf(L("42")))
                                        )),
                                        L("-"),
                                        N("Number", listOf(L("1342")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputMultiplication() {
        val parser = parserForString("out 1.25*3.123")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Mul", listOf(
                                        N("Number", listOf(L("1.25"))),
                                        L("*"),
                                        N("Number", listOf(L("3.123")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputDivision() {
        val parser = parserForString("out 42/3.0")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Mul", listOf(
                                        N("Number", listOf(L("42"))),
                                        L("/"),
                                        N("Number", listOf(L("3.0")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputMultiMulDiv() {
        val parser = parserForString("out 42/12.11*33331/3.0")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Mul", listOf(
                                        N("Mul", listOf(
                                                N("Mul", listOf(
                                                        N("Number", listOf(L("42"))),
                                                        L("/"),
                                                        N("Number", listOf(L("12.11")))
                                                )),
                                                L("*"),
                                                N("Number", listOf(L("33331")))
                                        )),
                                        L("/"),
                                        N("Number", listOf(L("3.0")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputPrecedenceSumMulA() {
        val parser = parserForString("out 1+2*4")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Sum", listOf(
                                        N("Number", listOf(L("1"))),
                                        L("+"),
                                        N("Mul", listOf(
                                                N("Number", listOf(L("2"))),
                                                L("*"),
                                                N("Number", listOf(L("4")))
                                        ))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputPrecedenceSumMulB() {
        val parser = parserForString("out 1*2-4")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Sum", listOf(
                                        N("Mul", listOf(
                                                N("Number", listOf(L("1"))),
                                                L("*"),
                                                N("Number", listOf(L("2")))
                                        )),
                                        L("-"),
                                        N("Number", listOf(L("4")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputPower() {
        val parser = parserForString("out 25^3")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Pow", listOf(
                                        N("Number", listOf(L("25"))),
                                        L("^"),
                                        N("Number", listOf(L("3")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputPrecedencePowerA() {
        val parser = parserForString("out 0*2^1.11")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Mul", listOf(
                                        N("Number", listOf(L("0"))),
                                        L("*"),
                                        N("Pow", listOf(
                                                N("Number", listOf(L("2"))),
                                                L("^"),
                                                N("Number", listOf(L("1.11")))
                                        ))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputPrecedencePowerB() {
        val parser = parserForString("out 0^2*1.11")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Mul", listOf(
                                        N("Pow", listOf(
                                                N("Number", listOf(L("0"))),
                                                L("^"),
                                                N("Number", listOf(L("2")))
                                        )),
                                        L("*"),
                                        N("Number", listOf(L("1.11")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputParen() {
        val parser = parserForString("out (42)")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Paren", listOf(
                                        L("("),
                                        N("Number", listOf(L("42"))),
                                        L(")")
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputUnbalancedParen() {
        val parser = parserForString("out (1+2*4")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

    @Test
    fun outputPrecedenceParenA() {
        val parser = parserForString("out (1+2)*4")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Mul", listOf(
                                        N("Paren", listOf(
                                                L("("),
                                                N("Sum", listOf(
                                                        N("Number", listOf(L("1"))),
                                                        L("+"),
                                                        N("Number", listOf(L("2")))
                                                )),
                                                L(")")
                                        )),
                                        L("*"),
                                        N("Number", listOf(L("4")))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputPrecedenceParenB() {
        val parser = parserForString("out 1^(2*4)")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Pow", listOf(
                                        N("Number", listOf(L("1"))),
                                        L("^"),
                                        N("Paren", listOf(
                                                L("("),
                                                N("Mul", listOf(
                                                        N("Number", listOf(L("2"))),
                                                        L("*"),
                                                        N("Number", listOf(L("4")))
                                                )),
                                                L(")")
                                        ))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputSequence() {
        val parser = parserForString("out {12, 42}")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Seq", listOf(
                                        L("{"),
                                        N("Number", listOf(L("12"))),
                                        L(","),
                                        N("Number", listOf(L("42"))),
                                        L("}")
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputSequenceNestedExpression() {
        val parser = parserForString("out {12-4, 42^3}")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Seq", listOf(
                                        L("{"),
                                        N("Sum", listOf(
                                                N("Number", listOf(L("12"))),
                                                L("-"),
                                                N("Number", listOf(L("4")))
                                        )),
                                        L(","),
                                        N("Pow", listOf(
                                                N("Number", listOf(L("42"))),
                                                L("^"),
                                                N("Number", listOf(L("3")))
                                        )),
                                        L("}")
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputUnbalancedSequence() {
        val parser = parserForString("out {12+23, 42 print \"a\"")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

    @Test
    fun newVariable() {
        val parser = parserForString("var _mySequence1 = {2, 12^3}")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Var", listOf(
                                L("var"),
                                L("_mySequence1"),
                                L("="),
                                N("Seq", listOf(
                                        L("{"),
                                        N("Number", listOf(L("2"))),
                                        L(","),
                                        N("Pow", listOf(
                                                N("Number", listOf(L("12"))),
                                                L("^"),
                                                N("Number", listOf(L("3")))
                                        )),
                                        L("}")
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun newVariableInvalidIdentifierA() {
        val parser = parserForString("var 1mySequence = {2, 12^3}")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

    @Test
    fun newVariableInvalidIdentifierB() {
        val parser = parserForString("var my-Sequence = {2, 12^3}")

        parser.program()

        assertParserSyntaxErrors(parser)
    }

    @Test
    fun outputIdentifierAsExpression() {
        val parser = parserForString("out 0*myVal^1.11")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Mul", listOf(
                                        N("Number", listOf(L("0"))),
                                        L("*"),
                                        N("Pow", listOf(
                                                N("Ref", listOf(L("myVal"))),
                                                L("^"),
                                                N("Number", listOf(L("1.11")))
                                        ))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun newVariableFromMap() {
        val parser = parserForString("var sequence = map({0, n}, i -> (-1)^i / (2 * i + 1))")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Var", listOf(
                                L("var"),
                                L("sequence"),
                                L("="),
                                N("Fun", listOf(
                                        L("map"),
                                        L("("),
                                        N("Seq", listOf(
                                                L("{"),
                                                N("Number", listOf(L("0"))),
                                                L(","),
                                                N("Ref", listOf(L("n"))),
                                                L("}")
                                        )),
                                        L(","),
                                        N("Lam", listOf(
                                                L("i"),
                                                L("->"),
                                                N("Mul", listOf(
                                                        N("Pow", listOf(
                                                                N("Paren", listOf(
                                                                        L("("),
                                                                        N("Number", listOf(L("-"), L("1"))),
                                                                        L(")")
                                                                )),
                                                                L("^"),
                                                                N("Ref", listOf(L("i")))
                                                        )),
                                                        L("/"),
                                                        N("Paren", listOf(
                                                                L("("),
                                                                N("Sum", listOf(
                                                                        N("Mul", listOf(
                                                                                N("Number", listOf(L("2"))),
                                                                                L("*"),
                                                                                N("Ref", listOf(L("i")))
                                                                        )),
                                                                        L("+"),
                                                                        N("Number", listOf(L("1")))
                                                                )),
                                                                L(")")
                                                        ))
                                                ))
                                        )),
                                        L(")")
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }

    @Test
    fun outputReduce() {
        val parser = parserForString("out 4 * reduce(sequence, 0, x y -> x + y)")

        val program = parser.program()

        Assert.assertEquals(
                N("Program", listOf(
                        N("Out", listOf(
                                L("out"),
                                N("Mul", listOf(
                                        N("Number", listOf(L("4"))),
                                        L("*"),
                                        N("Fun", listOf(
                                                L("reduce"),
                                                L("("),
                                                N("Ref", listOf(L("sequence"))),
                                                L(","),
                                                N("Number", listOf(L("0"))),
                                                L(","),
                                                N("Lam", listOf(
                                                        L("x"),
                                                        L("y"),
                                                        L("->"),
                                                        N("Sum", listOf(
                                                                N("Ref", listOf(L("x"))),
                                                                L("+"),
                                                                N("Ref", listOf(L("y")))
                                                        ))
                                                )),
                                                L(")")
                                        ))
                                ))
                        )),
                        EOF
                )),
                toTestTree(program))
        assertNoParserSyntaxErrors(parser)
    }
}
