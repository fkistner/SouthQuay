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

        Assert.assertEquals(Program(listOf(PrintStatement("a"))),
                ast)
    }
}