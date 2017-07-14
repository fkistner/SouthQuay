package com.fkistner.SouthQuay.parser

import org.junit.*


class ASTVisitorTests {
    @Test
    fun visitVariableRef() {
        val node = VariableRef("n")

        val visitor = object : CountingVisitor() {
            override fun visit(variableRef: VariableRef) {
                Assert.assertEquals(1, ++visitCounter)
                variableRef.acceptChildren(this)
            }

            override fun endVisit(variableRef: VariableRef) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitLambda() {
        val node = Lambda(listOf(VarDeclaration("a", Type.Integer)), VariableRef("n"))

        val visitor = object : CountingVisitor() {
            override fun visit(lambda: Lambda) {
                Assert.assertEquals(1, ++visitCounter)
                lambda.acceptChildren(this)
            }

            override fun endVisit(lambda: Lambda) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(varDeclaration: VarDeclaration) {
                Assert.assertEquals(2, ++visitCounter)
                varDeclaration.acceptChildren(this)
            }

            override fun endVisit(varDeclaration: VarDeclaration) {
                Assert.assertEquals(1, ++endVisitCounter)
            }

            override fun visit(variableRef: VariableRef) {
                Assert.assertEquals(3, ++visitCounter)
            }

            override fun endVisit(variableRef: VariableRef) {
                Assert.assertEquals(2, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
        Assert.assertEquals(3, visitor.endVisitCounter)
    }

    @Test
    fun visitLambdaNoChild() {
        val node = Lambda(listOf(VarDeclaration("a", Type.Integer), VarDeclaration("b", Type.Integer)),
                VariableRef("n"))

        val visitor = object : CountingVisitor() {
            override fun visit(lambda: Lambda) {
                Assert.assertEquals(1, ++visitCounter)
            }

            override fun endVisit(lambda: Lambda) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitFunctionInvoc() {
        val node = FunctionInvoc("a", listOf(VariableRef("i"),
                Lambda(listOf(VarDeclaration("a", Type.Integer),
                        VarDeclaration("b", Type.Integer)), VariableRef("n"))))

        val visitor = object : CountingVisitor() {
            override fun visit(functionInvoc: FunctionInvoc) {
                Assert.assertEquals(1, ++visitCounter)
                functionInvoc.acceptChildren(this)
            }

            override fun endVisit(functionInvoc: FunctionInvoc) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(lambda: Lambda) {
                Assert.assertEquals(3, ++visitCounter)
            }

            override fun endVisit(lambda: Lambda) {
                Assert.assertEquals(2, ++endVisitCounter)
            }

            override fun visit(variableRef: VariableRef) {
                Assert.assertEquals(2, ++visitCounter)
            }

            override fun endVisit(variableRef: VariableRef) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
        Assert.assertEquals(3, visitor.endVisitCounter)
    }

    @Test
    fun visitFunctionInvocNoChildren() {
        val node = FunctionInvoc("a", listOf(VariableRef("i"),
                Lambda(listOf(VarDeclaration("a", Type.Integer),
                        VarDeclaration("b", Type.Integer)), VariableRef("n"))))

        val visitor = object : CountingVisitor() {
            override fun visit(functionInvoc: FunctionInvoc) {
                Assert.assertEquals(1, ++visitCounter)
            }

            override fun endVisit(functionInvoc: FunctionInvoc) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitBinaryOperations() {
        val left = VariableRef("a")
        val right = FunctionInvoc("f", listOf(VariableRef("a")))
        val ops = listOf(Sum(left, right), Sub(left, right), Mul(left, right), Div(left, right), Pow(left, right))

        ops.map { op ->

            val visitor = object : CountingVisitor() {
                override fun visit(sum: Sum) {
                    ++visitCounter
                    if (op == sum) Assert.assertEquals(1, visitCounter)
                    sum.acceptChildren(this)
                }

                override fun endVisit(sum: Sum) {
                    ++endVisitCounter
                    if (op == sum) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(sub: Sub) {
                    ++visitCounter
                    if (op == sub) Assert.assertEquals(1, visitCounter)
                    sub.acceptChildren(this)
                }

                override fun endVisit(sub: Sub) {
                    ++endVisitCounter
                    if (op == sub) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(mul: Mul) {
                    ++visitCounter
                    if (op == mul) Assert.assertEquals(1, visitCounter)
                    mul.acceptChildren(this)
                }

                override fun endVisit(mul: Mul) {
                    ++endVisitCounter
                    if (op == mul) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(div: Div) {
                    ++visitCounter
                    if (op == div) Assert.assertEquals(1, visitCounter)
                    div.acceptChildren(this)
                }

                override fun endVisit(div: Div) {
                    ++endVisitCounter
                    if (op == div) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(pow: Pow) {
                    ++visitCounter
                    if (op == pow) Assert.assertEquals(1, visitCounter)
                    pow.acceptChildren(this)
                }

                override fun endVisit(pow: Pow) {
                    ++endVisitCounter
                    if (op == pow) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(variableRef: VariableRef) {
                    Assert.assertEquals(2, ++visitCounter)
                }

                override fun endVisit(variableRef: VariableRef) {
                    Assert.assertEquals(1, ++endVisitCounter)
                }

                override fun visit(functionInvoc: FunctionInvoc) {
                    Assert.assertEquals(3, ++visitCounter)
                }

                override fun endVisit(functionInvoc: FunctionInvoc) {
                    Assert.assertEquals(2, ++endVisitCounter)
                }
            }
            op.accept(visitor)

            Assert.assertEquals(3, visitor.visitCounter)
            Assert.assertEquals(3, visitor.endVisitCounter)
        }
    }

    @Test
    fun visitBinaryOperationNoChildren() {
        val left = VariableRef("a")
        val right = FunctionInvoc("f", listOf(VariableRef("a")))
        val ops = listOf(Sum(left, right), Sub(left, right), Mul(left, right), Div(left, right), Pow(left, right))

        ops.map { op ->

            val visitor = object : CountingVisitor() {
                override fun visit(sum: Sum) {
                    ++visitCounter
                    if (op == sum) Assert.assertEquals(1, visitCounter)
                }

                override fun endVisit(sum: Sum) {
                    ++endVisitCounter
                    if (op == sum) Assert.assertEquals(1, endVisitCounter)
                }

                override fun visit(sub: Sub) {
                    ++visitCounter
                    if (op == sub) Assert.assertEquals(1, visitCounter)
                }

                override fun endVisit(sub: Sub) {
                    ++endVisitCounter
                    if (op == sub) Assert.assertEquals(1, endVisitCounter)
                }

                override fun visit(mul: Mul) {
                    ++visitCounter
                    if (op == mul) Assert.assertEquals(1, visitCounter)
                }

                override fun endVisit(mul: Mul) {
                    ++endVisitCounter
                    if (op == mul) Assert.assertEquals(1, endVisitCounter)
                }

                override fun visit(div: Div) {
                    ++visitCounter
                    if (op == div) Assert.assertEquals(1, visitCounter)
                }

                override fun endVisit(div: Div) {
                    ++endVisitCounter
                    if (op == div) Assert.assertEquals(1, endVisitCounter)
                }

                override fun visit(pow: Pow) {
                    ++visitCounter
                    if (op == pow) Assert.assertEquals(1, visitCounter)
                }

                override fun endVisit(pow: Pow) {
                    ++endVisitCounter
                    if (op == pow) Assert.assertEquals(1, endVisitCounter)
                }
            }
            op.accept(visitor)

            Assert.assertEquals(1, visitor.visitCounter)
            Assert.assertEquals(1, visitor.endVisitCounter)
        }
    }

    @Test
    fun visitSequence() {
        val node = Sequence(VariableRef("a"), Sum(VariableRef("l"), VariableRef("r")))

        val visitor = object : CountingVisitor() {
            override fun visit(sequence: Sequence) {
                Assert.assertEquals(1, ++visitCounter)
                sequence.acceptChildren(this)
            }

            override fun endVisit(sequence: Sequence) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(variableRef: VariableRef) {
                Assert.assertEquals(2, ++visitCounter)
            }

            override fun endVisit(variableRef: VariableRef) {
                Assert.assertEquals(1, ++endVisitCounter)
            }

            override fun visit(sum: Sum) {
                Assert.assertEquals(3, ++visitCounter)
            }

            override fun endVisit(sum: Sum) {
                Assert.assertEquals(2, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
        Assert.assertEquals(3, visitor.endVisitCounter)
    }

    @Test
    fun visitSequenceNoChildren() {
        val node = Sequence(VariableRef("a"), Sum(VariableRef("l"), VariableRef("r")))

        val visitor = object : CountingVisitor() {
            override fun visit(sequence: Sequence) {
                Assert.assertEquals(1, ++visitCounter)
            }

            override fun endVisit(sequence: Sequence) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitRealLiteral() {
        val node = RealLiteral(1.23)

        val visitor = object : CountingVisitor() {
            override fun visit(realLiteral: RealLiteral) {
                Assert.assertEquals(1, ++visitCounter)
                realLiteral.acceptChildren(this)
            }

            override fun endVisit(realLiteral: RealLiteral) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitIntegerLiteral() {
        val node = IntegerLiteral(5)

        val visitor = object : CountingVisitor() {
            override fun visit(integerLiteral: IntegerLiteral) {
                Assert.assertEquals(1, ++visitCounter)
                integerLiteral.acceptChildren(this)
            }

            override fun endVisit(integerLiteral: IntegerLiteral) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitVarStatement() {
        val node = VarStatement(VarDeclaration("a", Type.Integer), IntegerLiteral(5))

        val visitor = object : CountingVisitor() {
            override fun visit(varStatement: VarStatement) {
                Assert.assertEquals(1, ++visitCounter)
                varStatement.acceptChildren(this)
            }

            override fun endVisit(varStatement: VarStatement) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(varDeclaration: VarDeclaration) {
                Assert.assertEquals(2, ++visitCounter)
            }

            override fun endVisit(varDeclaration: VarDeclaration) {
                Assert.assertEquals(1, ++endVisitCounter)
            }

            override fun visit(integerLiteral: IntegerLiteral) {
                Assert.assertEquals(3, ++visitCounter)
            }

            override fun endVisit(integerLiteral: IntegerLiteral) {
                Assert.assertEquals(2, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
        Assert.assertEquals(3, visitor.endVisitCounter)
    }

    @Test
    fun visitVarStatementNoChild() {
        val node = VarStatement(VarDeclaration("a", Type.Integer), IntegerLiteral(5))

        val visitor = object : CountingVisitor() {
            override fun visit(varStatement: VarStatement) {
                Assert.assertEquals(1, ++visitCounter)
            }

            override fun endVisit(varStatement: VarStatement) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitOutStatement() {
        val node = OutStatement(IntegerLiteral(5))

        val visitor = object : CountingVisitor() {
            override fun visit(outStatement: OutStatement) {
                Assert.assertEquals(1, ++visitCounter)
                outStatement.acceptChildren(this)
            }

            override fun endVisit(outStatement: OutStatement) {
                Assert.assertEquals(2, ++endVisitCounter)
            }

            override fun visit(integerLiteral: IntegerLiteral) {
                Assert.assertEquals(2, ++visitCounter)
            }

            override fun endVisit(integerLiteral: IntegerLiteral) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(2, visitor.visitCounter)
        Assert.assertEquals(2, visitor.endVisitCounter)
    }

    @Test
    fun visitOutStatementNoChild() {
        val node = OutStatement(IntegerLiteral(5))

        val visitor = object : CountingVisitor() {
            override fun visit(outStatement: OutStatement) {
                Assert.assertEquals(1, ++visitCounter)
            }

            override fun endVisit(outStatement: OutStatement) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitPrintStatement() {
        val node = PrintStatement("abc")

        val visitor = object : CountingVisitor() {
            override fun visit(printStatement: PrintStatement) {
                Assert.assertEquals(1, ++visitCounter)
                printStatement.acceptChildren(this)
            }

            override fun endVisit(printStatement: PrintStatement) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitProgram() {
        val node = Program(listOf(PrintStatement("abc"), OutStatement(IntegerLiteral(5))))

        val visitor = object : CountingVisitor() {
            override fun visit(program: Program) {
                Assert.assertEquals(1, ++visitCounter)
                program.acceptChildren(this)
            }

            override fun endVisit(program: Program) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(printStatement: PrintStatement) {
                Assert.assertEquals(2, ++visitCounter)
            }

            override fun endVisit(printStatement: PrintStatement) {
                Assert.assertEquals(1, ++endVisitCounter)
            }

            override fun visit(outStatement: OutStatement) {
                Assert.assertEquals(3, ++visitCounter)
            }

            override fun endVisit(outStatement: OutStatement) {
                Assert.assertEquals(2, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
        Assert.assertEquals(3, visitor.endVisitCounter)
    }

    @Test
    fun visitProgramNoChildren() {
        val node = Program(listOf(PrintStatement("abc"), OutStatement(IntegerLiteral(5))))

        val visitor = object : CountingVisitor() {
            override fun visit(program: Program) {
                Assert.assertEquals(1, ++visitCounter)
            }

            override fun endVisit(program: Program) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(1, visitor.visitCounter)
        Assert.assertEquals(1, visitor.endVisitCounter)
    }

    @Test
    fun visitNestedHierarchies() {
        val node = Program(listOf(OutStatement(VariableRef("n"))))

        val visitor = object : CountingVisitor() {
            override fun visit(program: Program) {
                Assert.assertEquals(1, ++visitCounter)
                program.acceptChildren(this)
            }

            override fun endVisit(program: Program) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(outStatement: OutStatement) {
                Assert.assertEquals(2, ++visitCounter)
                outStatement.acceptChildren(this)
            }

            override fun endVisit(outStatement: OutStatement) {
                Assert.assertEquals(2, ++endVisitCounter)
            }

            override fun visit(variableRef: VariableRef) {
                Assert.assertEquals(3, ++visitCounter)
                variableRef.acceptChildren(this)
            }

            override fun endVisit(variableRef: VariableRef) {
                Assert.assertEquals(1, ++endVisitCounter)
            }
        }
        node.accept(visitor)

        Assert.assertEquals(3, visitor.visitCounter)
        Assert.assertEquals(3, visitor.endVisitCounter)
    }
}