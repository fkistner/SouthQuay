package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*
import org.junit.*


class InterpreterTests {

    @Test
    fun printString() {
        val (program, _) = ASTBuilder.parseText("print \"Test Output\"")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("Test Output", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = Interpreter(participant)
        interpreter.execute(program)

        Assert.assertEquals(1, participant.statementCounter)
    }

}