package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*
import org.junit.*


class InterpreterTests {

    @Test
    fun printString() {
        val (program, errors) = ASTBuilder.parseText("print \"Test Output\"")
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
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun outputIntegerLiteral() {
        val (program, errors) = ASTBuilder.parseText("out 10")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("10", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun outputRealLiteral() {
        val (program, errors) = ASTBuilder.parseText("out 3.14")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("3.14", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun outputIntegerMath() {
        val (program, errors) = ASTBuilder.parseText("out (1+2-3*4^5)/3")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("-1023", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun outputIntPow() {
        val (program, errors) = ASTBuilder.parseText("out 2^(-1)^4")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("2", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun outputMixedMath() {
        val (program, errors) = ASTBuilder.parseText("out (1+2.0-3*4^5)/3")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("-1023.0", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun outputSequence() {
        val (program, errors) = ASTBuilder.parseText("out {1, 2 * 3}")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("{1, 2, 3, …, 6}", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun outputShortSequence() {
        val (program, errors) = ASTBuilder.parseText("out {0, 4}")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("{0, 1, 2, 3, 4}", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun saveLoadVariable() {
        val (program, errors) = ASTBuilder.parseText("var i = 3*12 out i")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(2, statementCounter)
                Assert.assertEquals("36", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                when (statement) {
                    is VarStatement -> {
                        Assert.assertEquals(1, statementCounter)
                        Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
                    }
                    is OutStatement -> {
                        Assert.assertEquals(2, statementCounter)
                        Assert.assertTrue("Wrong reference.", program.statements[1] === statement)
                    }
                }

            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(2, participant.statementCounter)
    }

    @Test
    fun applyFunction() {
        val (program, errors) = ASTBuilder.parseText("var i = 11 out apply(2+3, i -> i*i)")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(2, statementCounter)
                Assert.assertEquals("25", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                when (statement) {
                    is VarStatement -> {
                        Assert.assertEquals(1, statementCounter)
                        Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
                    }
                    is OutStatement -> {
                        Assert.assertEquals(2, statementCounter)
                        Assert.assertTrue("Wrong reference.", program.statements[1] === statement)
                    }
                }

            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(2, participant.statementCounter)
    }

    @Test
    fun mapSequence() {
        val (program, errors) = ASTBuilder.parseText("out map({1, 2 * 3}, i -> i*i)")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("{1, 4, 9, …, 36}", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun reduceSequence() {
        val (program, errors) = ASTBuilder.parseText("out reduce({1, 100}, 0, a i -> a+i)")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("5050", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun reduceSequenceReal() {
        val (program, errors) = ASTBuilder.parseText("out reduce({1, 10}, 1.0, a i -> a*i)/1000")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("3628.8", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test
    fun mapToRealSequence() {
        val (program, errors) = ASTBuilder.parseText("out map({1, 5}, i -> i*1.25)")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("{1.25, 2.5, 3.75, 5.0, 6.25}", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test(timeout = 1000L)
    fun mapLargeSequence() {
        val (program, errors) = ASTBuilder.parseText("out map({1, 1000000000}, i -> i+i)")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("{2, 4, 6, …, 2000000000}", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
    }

    @Test(expected = ArithmeticException::class)
    fun arithmeticError() {
        val (program, _) = ASTBuilder.parseText("out 1/0")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.fail("Should not be called.")
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)

        try {
            interpreter.execute(program)
        } finally {
            Assert.assertEquals(1, participant.statementCounter)
        }
    }

    @Test
    fun arithmeticErrorWithErrorContainer() {
        val (program, errors) = ASTBuilder.parseText("out 1/0")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.fail("Should not be called.")
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
        Assert.assertEquals(1, errors.count())
        Assert.assertEquals(Span(Position(1, 4, 4), 3), errors[0].span)
        Assert.assertTrue("Exception not recorded.", errors[0].message!!.startsWith("/ by zero"))
    }

    @Test
    fun arithmeticErrorInLambdaWithErrorContainer() {
        val (program, errors) = ASTBuilder.parseText("out map({1, 10}, i -> i/0)")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.fail("Should not be called.")
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
        Assert.assertEquals(1, errors.count())
        Assert.assertEquals(Span(Position(1, 22, 22), 3), errors[0].span)
        Assert.assertTrue("Exception not recorded.", errors[0].message!!.startsWith("/ by zero"))
    }

    @Test
    fun nestedSequences() {
        val (program, errors) = ASTBuilder.parseText("out map({1,10}, c -> reduce({1,c}, 0, a b -> a+b))")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("{1, 3, 6, …, 55}", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
        Assert.assertEquals(0, errors.count())
    }

    @Test
    fun mapReal() {
        val (program, errors) = ASTBuilder.parseText("out map(map({1,10}, i -> i+1.0), x -> x*x)")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("{4.0, 9.0, 16.0, …, 121.0}", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
        Assert.assertEquals(0, errors.count())
    }

    @Test
    fun reduceReal() {
        val (program, errors) = ASTBuilder.parseText("out reduce(map({1,10}, i -> i/10.0), 1, a x -> a*x)")
        program!!

        val participant = object : CountingParticipant() {
            override fun output(statement: Statement, string: String) {
                Assert.assertEquals(1, statementCounter)
                Assert.assertEquals("3.6288000000000005E-4", string)
            }

            override fun statementExecuting(statement: Statement) {
                super.statementExecuting(statement)
                Assert.assertEquals(1, statementCounter)
                Assert.assertTrue("Wrong reference.", program.statements[0] === statement)
            }
        }
        val interpreter = StatementInterpreter(participant)
        interpreter.execute(program, errors)

        Assert.assertEquals(1, participant.statementCounter)
        Assert.assertEquals(0, errors.count())
    }
}