package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.parser.*
import org.junit.*

class ASTTests {

    @Test
    fun empty() {
        val parser = parserForString("")

        val ast = parser.toAST()

        Assert.assertEquals(Program(),
                ast)
    }

    @Test
    fun printStatement() {
        val parser = parserForString("print \"a\"")

        val ast = parser.toAST()

        Assert.assertEquals(
                Program(listOf(
                        PrintStatement("a")
                )),
                ast)
    }

    @Test
    fun outputInteger() {
        val parser = parserForString("out 10")

        val ast = parser.toAST()

        Assert.assertEquals(Program(listOf(
                    OutStatement(IntegerLiteral(10))
                )),
                ast)
    }

    @Test
    fun outputNegativeInteger() {
        val parser = parserForString("out -10")

        val ast = parser.toAST()

        Assert.assertEquals(Program(listOf(
                    OutStatement(IntegerLiteral(-10))
                )),
                ast)
    }

    @Test
    fun outputReal() {
        val parser = parserForString("out 10.123")

        val ast = parser.toAST()

        Assert.assertEquals(Program(listOf(
                    OutStatement(RealLiteral(10.123))
                )),
                ast)
    }

    @Test
    fun outputNegativeReal() {
        val parser = parserForString("out -10.123")

        val ast = parser.toAST()

        Assert.assertEquals(Program(listOf(
                    OutStatement(RealLiteral(-10.123))
                )),
                ast)
    }

    @Test
    fun newVariableFromSequence() {
        val parser = parserForString("var myVar1 = {3, 10}")

        val ast = parser.toAST()

        Assert.assertEquals(Program(listOf(
                    VarStatement("myVar1", Sequence(IntegerLiteral(3), IntegerLiteral(10)))
                )),
                ast)
    }

    @Test
    fun outputMathOperation() {
        val parser = parserForString("out 2*(1+2-3*4/5^6)")

        val ast = parser.toAST()

        Assert.assertEquals(Program(listOf(
                    OutStatement(
                            Mul(
                                    IntegerLiteral(2),
                                    Sub(
                                            Sum(IntegerLiteral(1), IntegerLiteral(2)),
                                            Div(
                                                    Mul(IntegerLiteral(3), IntegerLiteral(4)),
                                                    Pow(IntegerLiteral(5), IntegerLiteral(6))
                                            )
                                    )
                            )
                    )
                )),
                ast)
    }
}