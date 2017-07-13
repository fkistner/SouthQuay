package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.parser.*
import org.antlr.v4.runtime.*
import org.junit.*
import java.io.StringReader

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

    @Test
    fun newVariableFromMap() {
        val parser = parserForString("var i = map({1, 4}, n -> 2 * n)")

        val ast = parser.toAST()

        Assert.assertEquals(Program(listOf(
                    VarStatement("i", FunctionInvoc("map", listOf(
                            Sequence(IntegerLiteral(1), IntegerLiteral(4)),
                            Lambda(listOf("n"), Mul(IntegerLiteral(2), VariableRef("n")))
                    )))
                )),
                ast)
    }

    @Test
    fun invalidToken() {
        val charStream = CharStreams.fromReader(StringReader("#"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(1, error.line)
        Assert.assertEquals(0, error.column)
        Assert.assertTrue("Extraneous input not detected.", error.message?.startsWith("extraneous input '#'") == true)
    }

    @Test
    fun invalidTokenInVariableDeclaration() {
        val charStream = CharStreams.fromReader(StringReader("var \""))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(1, error.line)
        Assert.assertEquals(4, error.column)
        Assert.assertTrue("Mismatched input not detected.", error.message?.startsWith("mismatched input '\"'") == true)
    }

    @Test
    fun unbalancedStringLiteral() {
        val charStream = CharStreams.fromReader(StringReader("print \"Test\nout {3, 6}"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(1, error.line)
        Assert.assertEquals(6, error.column)
        Assert.assertTrue("Mismatched input not detected.", error.message?.startsWith("mismatched input '\"'") == true)
    }

    @Test
    fun outMissingIdentifier() {
        val charStream = CharStreams.fromReader(StringReader("out print \"Test\"\nout {3, 6}"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(1, error.line)
        Assert.assertEquals(4, error.column)
        Assert.assertTrue("Mismatched input not detected.", error.message?.startsWith("mismatched input 'print'") == true)
    }

    @Test
    fun multipleErrors() {
        val charStream = CharStreams.fromReader(StringReader("out print \"Test\"\nout {\"Hello\", 6}"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(2, errors.count())
        val errorA = errors[0]
        Assert.assertEquals(1, errorA.line)
        Assert.assertEquals(4, errorA.column)
        Assert.assertTrue("Mismatched input not detected.", errorA.message?.startsWith("mismatched input 'print'") == true)
        val errorB = errors[1]
        Assert.assertEquals(2, errorB.line)
        Assert.assertEquals(5, errorB.column)
        Assert.assertTrue("Mismatched input not detected.", errorB.message?.startsWith("mismatched input '\"Hello\"'") == true)
    }
}