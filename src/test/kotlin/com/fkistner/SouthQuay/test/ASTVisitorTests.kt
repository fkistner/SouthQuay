package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.parser.*
import org.junit.*


class ASTVisitorTests {
    @Test
    fun visitVariableRef() {
        val node = VariableRef("n")

        val visitor = object : CountingVisitor() {
            override fun visit(variableRef: VariableRef): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitLambda() {
        val node = Lambda(listOf("a", "b"), VariableRef("n"))

        val visitor = object : CountingVisitor() {
            override fun visit(lambda: Lambda): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun visit(variableRef: VariableRef): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(2, visitor.visitCounter)
    }

    @Test
    fun visitLambdaNoChild() {
        val node = Lambda(listOf("a", "b"), VariableRef("n"))

        val visitor = object : CountingVisitor() {
            override fun visit(lambda: Lambda): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitFunctionInvoc() {
        val node = FunctionInvoc("a", listOf(VariableRef("i"), Lambda(listOf("a", "b"), VariableRef("n"))))

        val visitor = object : CountingVisitor() {
            override fun visit(functionInvoc: FunctionInvoc): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun visit(lambda: Lambda): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return false
            }

            override fun visit(variableRef: VariableRef): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
    }

    @Test
    fun visitFunctionInvocNoChildren() {
        val node = FunctionInvoc("a", listOf(VariableRef("i"), Lambda(listOf("a", "b"), VariableRef("n"))))

        val visitor = object : CountingVisitor() {
            override fun visit(functionInvoc: FunctionInvoc): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitBinaryOperations() {
        val left = VariableRef("a")
        val right = FunctionInvoc("f", listOf(VariableRef("a")))
        val ops = listOf(Sum(left, right), Sub(left, right), Mul(left, right), Div(left, right), Pow(left, right))

        ops.map { op ->

            val visitor = object : CountingVisitor() {
                override fun visit(sum: Sum): Boolean {
                    if (op == sum) Assert.assertEquals(1, ++visitCounter)
                    return true
                }

                override fun visit(sub: Sub): Boolean {
                    if (op == sub) Assert.assertEquals(1, ++visitCounter)
                    return true
                }

                override fun visit(mul: Mul): Boolean {
                    if (op == mul) Assert.assertEquals(1, ++visitCounter)
                    return true
                }

                override fun visit(div: Div): Boolean {
                    if (op == div) Assert.assertEquals(1, ++visitCounter)
                    return true
                }

                override fun visit(pow: Pow): Boolean {
                    if (op == pow) Assert.assertEquals(1, ++visitCounter)
                    return true
                }

                override fun visit(variableRef: VariableRef): Boolean {
                    Assert.assertEquals(2, ++visitCounter)
                    return false
                }

                override fun visit(functionInvoc: FunctionInvoc): Boolean {
                    Assert.assertEquals(3, ++visitCounter)
                    return false
                }
            }
            op.accept(visitor)

            Assert.assertEquals(3, visitor.visitCounter)
        }
    }

    @Test
    fun visitBinaryOperationNoChildren() {
        val left = VariableRef("a")
        val right = FunctionInvoc("f", listOf(VariableRef("a")))
        val ops = listOf(Sum(left, right), Sub(left, right), Mul(left, right), Div(left, right), Pow(left, right))

        ops.map { op ->

            val visitor = object : CountingVisitor() {
                override fun visit(sum: Sum): Boolean {
                    if (op == sum) Assert.assertEquals(1, ++visitCounter)
                    return false
                }

                override fun visit(sub: Sub): Boolean {
                    if (op == sub) Assert.assertEquals(1, ++visitCounter)
                    return false
                }

                override fun visit(mul: Mul): Boolean {
                    if (op == mul) Assert.assertEquals(1, ++visitCounter)
                    return false
                }

                override fun visit(div: Div): Boolean {
                    if (op == div) Assert.assertEquals(1, ++visitCounter)
                    return false
                }

                override fun visit(pow: Pow): Boolean {
                    if (op == pow) Assert.assertEquals(1, ++visitCounter)
                    return false
                }
            }
            op.accept(visitor)

            Assert.assertEquals(1, visitor.visitCounter)
        }
    }

    @Test
    fun visitSequence() {
        val node = Sequence(VariableRef("a"), Sum(VariableRef("l"), VariableRef("r")))

        val visitor = object : CountingVisitor() {
            override fun visit(sequence: Sequence): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun visit(variableRef: VariableRef): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }

            override fun visit(sum: Sum): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
    }

    @Test
    fun visitSequenceNoChildren() {
        val node = Sequence(VariableRef("a"), Sum(VariableRef("l"), VariableRef("r")))

        val visitor = object : CountingVisitor() {
            override fun visit(sequence: Sequence): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitRealLiteral() {
        val node = RealLiteral(1.23)

        val visitor = object : CountingVisitor() {
            override fun visit(realLiteral: RealLiteral): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitIntegerLiteral() {
        val node = IntegerLiteral(5)

        val visitor = object : CountingVisitor() {
            override fun visit(integerLiteral: IntegerLiteral): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitVarStatement() {
        val node = VarStatement("a", IntegerLiteral(5))

        val visitor = object : CountingVisitor() {
            override fun visit(varStatement: VarStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun visit(integerLiteral: IntegerLiteral): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(2, visitor.visitCounter)
    }

    @Test
    fun visitVarStatementNoChild() {
        val node = VarStatement("a", IntegerLiteral(5))

        val visitor = object : CountingVisitor() {
            override fun visit(varStatement: VarStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitOutStatement() {
        val node = OutStatement(IntegerLiteral(5))

        val visitor = object : CountingVisitor() {
            override fun visit(outStatement: OutStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun visit(integerLiteral: IntegerLiteral): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(2, visitor.visitCounter)
    }

    @Test
    fun visitOutStatementNoChild() {
        val node = OutStatement(IntegerLiteral(5))

        val visitor = object : CountingVisitor() {
            override fun visit(outStatement: OutStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitPrintStatement() {
        val node = PrintStatement("abc")

        val visitor = object : CountingVisitor() {
            override fun visit(printStatement: PrintStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }

    @Test
    fun visitProgram() {
        val node = Program(listOf(PrintStatement("abc"), OutStatement(IntegerLiteral(5))))

        val visitor = object : CountingVisitor() {
            override fun visit(program: Program): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun visit(printStatement: PrintStatement): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }

            override fun visit(outStatement: OutStatement): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
    }

    @Test
    fun visitProgramNoChildren() {
        val node = Program(listOf(PrintStatement("abc"), OutStatement(IntegerLiteral(5))))

        val visitor = object : CountingVisitor() {
            override fun visit(program: Program): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
    }
}