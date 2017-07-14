package com.fkistner.SouthQuay.parser

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
            override fun visit(lambda: Lambda): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun endVisit(lambda: Lambda) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(varDeclaration: VarDeclaration): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return true
            }

            override fun endVisit(varDeclaration: VarDeclaration) {
                Assert.assertEquals(1, ++endVisitCounter)
            }

            override fun visit(variableRef: VariableRef): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return false
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
            override fun visit(lambda: Lambda): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
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
            override fun visit(functionInvoc: FunctionInvoc): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun endVisit(functionInvoc: FunctionInvoc) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(lambda: Lambda): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return false
            }

            override fun endVisit(lambda: Lambda) {
                Assert.assertEquals(2, ++endVisitCounter)
            }

            override fun visit(variableRef: VariableRef): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
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
            override fun visit(functionInvoc: FunctionInvoc): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
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
                override fun visit(sum: Sum): Boolean {
                    ++visitCounter
                    if (op == sum) Assert.assertEquals(1, visitCounter)
                    return true
                }

                override fun endVisit(sum: Sum) {
                    ++endVisitCounter
                    if (op == sum) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(sub: Sub): Boolean {
                    ++visitCounter
                    if (op == sub) Assert.assertEquals(1, visitCounter)
                    return true
                }

                override fun endVisit(sub: Sub) {
                    ++endVisitCounter
                    if (op == sub) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(mul: Mul): Boolean {
                    ++visitCounter
                    if (op == mul) Assert.assertEquals(1, visitCounter)
                    return true
                }

                override fun endVisit(mul: Mul) {
                    ++endVisitCounter
                    if (op == mul) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(div: Div): Boolean {
                    ++visitCounter
                    if (op == div) Assert.assertEquals(1, visitCounter)
                    return true
                }

                override fun endVisit(div: Div) {
                    ++endVisitCounter
                    if (op == div) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(pow: Pow): Boolean {
                    ++visitCounter
                    if (op == pow) Assert.assertEquals(1, visitCounter)
                    return true
                }

                override fun endVisit(pow: Pow) {
                    ++endVisitCounter
                    if (op == pow) Assert.assertEquals(3, endVisitCounter)
                }

                override fun visit(variableRef: VariableRef): Boolean {
                    Assert.assertEquals(2, ++visitCounter)
                    return false
                }

                override fun endVisit(variableRef: VariableRef) {
                    Assert.assertEquals(1, ++endVisitCounter)
                }

                override fun visit(functionInvoc: FunctionInvoc): Boolean {
                    Assert.assertEquals(3, ++visitCounter)
                    return false
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
                override fun visit(sum: Sum): Boolean {
                    ++visitCounter
                    if (op == sum) Assert.assertEquals(1, visitCounter)
                    return false
                }

                override fun endVisit(sum: Sum) {
                    ++endVisitCounter
                    if (op == sum) Assert.assertEquals(1, endVisitCounter)
                }

                override fun visit(sub: Sub): Boolean {
                    ++visitCounter
                    if (op == sub) Assert.assertEquals(1, visitCounter)
                    return false
                }

                override fun endVisit(sub: Sub) {
                    ++endVisitCounter
                    if (op == sub) Assert.assertEquals(1, endVisitCounter)
                }

                override fun visit(mul: Mul): Boolean {
                    ++visitCounter
                    if (op == mul) Assert.assertEquals(1, visitCounter)
                    return false
                }

                override fun endVisit(mul: Mul) {
                    ++endVisitCounter
                    if (op == mul) Assert.assertEquals(1, endVisitCounter)
                }

                override fun visit(div: Div): Boolean {
                    ++visitCounter
                    if (op == div) Assert.assertEquals(1, visitCounter)
                    return false
                }

                override fun endVisit(div: Div) {
                    ++endVisitCounter
                    if (op == div) Assert.assertEquals(1, endVisitCounter)
                }

                override fun visit(pow: Pow): Boolean {
                    ++visitCounter
                    if (op == pow) Assert.assertEquals(1, visitCounter)
                    return false
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
            override fun visit(sequence: Sequence): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun endVisit(sequence: Sequence) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(variableRef: VariableRef): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }

            override fun endVisit(variableRef: VariableRef) {
                Assert.assertEquals(1, ++endVisitCounter)
            }

            override fun visit(sum: Sum): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return false
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
            override fun visit(sequence: Sequence): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
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
            override fun visit(realLiteral: RealLiteral): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
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
            override fun visit(integerLiteral: IntegerLiteral): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
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
            override fun visit(varStatement: VarStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun endVisit(varStatement: VarStatement) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(varDeclaration: VarDeclaration): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }

            override fun endVisit(varDeclaration: VarDeclaration) {
                Assert.assertEquals(1, ++endVisitCounter)
            }

            override fun visit(integerLiteral: IntegerLiteral): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return false
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
            override fun visit(varStatement: VarStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
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
            override fun visit(outStatement: OutStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun endVisit(outStatement: OutStatement) {
                Assert.assertEquals(2, ++endVisitCounter)
            }

            override fun visit(integerLiteral: IntegerLiteral): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
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
            override fun visit(outStatement: OutStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
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
            override fun visit(printStatement: PrintStatement): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
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
            override fun visit(program: Program): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun endVisit(program: Program) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(printStatement: PrintStatement): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return false
            }

            override fun endVisit(printStatement: PrintStatement) {
                Assert.assertEquals(1, ++endVisitCounter)
            }

            override fun visit(outStatement: OutStatement): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return false
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
            override fun visit(program: Program): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return false
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
            override fun visit(program: Program): Boolean {
                Assert.assertEquals(1, ++visitCounter)
                return true
            }

            override fun endVisit(program: Program) {
                Assert.assertEquals(3, ++endVisitCounter)
            }

            override fun visit(outStatement: OutStatement): Boolean {
                Assert.assertEquals(2, ++visitCounter)
                return true
            }

            override fun endVisit(outStatement: OutStatement) {
                Assert.assertEquals(2, ++endVisitCounter)
            }

            override fun visit(variableRef: VariableRef): Boolean {
                Assert.assertEquals(3, ++visitCounter)
                return true
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