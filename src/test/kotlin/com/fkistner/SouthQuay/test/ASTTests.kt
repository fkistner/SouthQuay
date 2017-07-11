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

}