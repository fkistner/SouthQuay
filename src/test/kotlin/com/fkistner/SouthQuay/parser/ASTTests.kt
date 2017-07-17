package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.interpreter.functions.*
import org.antlr.v4.runtime.CharStreams
import org.junit.*
import java.io.StringReader

class ASTTests {
    fun Pair<Program?, MutableList<SQLangError>>.toAST() = first

    @Test
    fun empty() {
        val (ast, _) = ASTBuilder.parseText("")

        Assert.assertEquals(Program(),
                ast)
    }

    @Test
    fun printStatement() {
        val (ast, _) = ASTBuilder.parseText("print \"a\"")

        Assert.assertEquals(
                Program(listOf(
                        PrintStatement("a")
                )),
                ast)
    }

    @Test
    fun outputInteger() {
        val (ast, _) = ASTBuilder.parseText("out 10")

        Assert.assertEquals(Program(listOf(
                    OutStatement(IntegerLiteral(10))
                )),
                ast)
    }

    @Test
    fun outputNegativeInteger() {
        val (ast, _) = ASTBuilder.parseText("out -10")

        Assert.assertEquals(Program(listOf(
                    OutStatement(IntegerLiteral(-10))
                )),
                ast)
    }

    @Test
    fun outputReal() {
        val (ast, _) = ASTBuilder.parseText("out 10.123")

        Assert.assertEquals(Program(listOf(
                    OutStatement(RealLiteral(10.123))
                )),
                ast)
    }

    @Test
    fun outputNegativeReal() {
        val (ast, _) = ASTBuilder.parseText("out -10.123")

        Assert.assertEquals(Program(listOf(
                    OutStatement(RealLiteral(-10.123))
                )),
                ast)
    }

    @Test
    fun newVariableFromSequence() {
        val (ast, _) = ASTBuilder.parseText("var myVar1 = {3, 10}")

        Assert.assertEquals(Program(listOf(
                    VarStatement(VarDeclaration("myVar1", Type.Sequence(Type.Integer)),
                            Sequence(IntegerLiteral(3), IntegerLiteral(10)))
                )),
                ast)
    }

    @Test
    fun outputMathOperation() {
        val (ast, _) = ASTBuilder.parseText("out 2*(1+2-3*4/5^6)")

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
    fun outputPowOperation() {
        val (ast, _) = ASTBuilder.parseText("out 2^(-1)^4")

        Assert.assertEquals(Program(listOf(
                OutStatement(
                        Pow(
                                IntegerLiteral(2),
                                Pow(
                                        IntegerLiteral(-1),
                                        IntegerLiteral(4)
                                )
                        )
                )
        )),
                ast)
    }

    @Test
    fun newVariableFromMap() {
        val (ast, _) = ASTBuilder.parseText("var i = map({1, 4}, n -> 2 * n)")

        Assert.assertEquals(Program(listOf(
                    VarStatement(VarDeclaration("i", Type.Sequence(Type.Integer)), FunctionInvoc("map", listOf(
                            Sequence(IntegerLiteral(1), IntegerLiteral(4)),
                            Lambda(listOf(VarDeclaration("n", Type.Integer)), Mul(IntegerLiteral(2), VariableRef("n")))
                    )))
                )),
                ast)
    }

    @Test
    fun nestedSequence() {
        val (ast, errors) = ASTBuilder.parseText("out map({1,10}, c -> reduce(map({1, 10}, i -> i^2), c*2, x y -> x+y))")

        Assert.assertEquals(Program(listOf(
                    OutStatement(FunctionInvoc("map", listOf(
                            Sequence(IntegerLiteral(1), IntegerLiteral(10)),
                            Lambda(listOf(VarDeclaration("c", Type.Integer)), FunctionInvoc("reduce", listOf(
                                    FunctionInvoc("map", listOf(
                                            Sequence(IntegerLiteral(1), IntegerLiteral(10)),
                                            Lambda(listOf(VarDeclaration("i", Type.Integer)), Pow(VariableRef("i"), IntegerLiteral(2)))
                                    )),
                                    Mul(VariableRef("c"), IntegerLiteral(2)),
                                    Lambda(listOf(VarDeclaration("x", Type.Integer), VarDeclaration("y", Type.Integer)), Sum(VariableRef("x"), VariableRef("y")))
                            )))
                    )))
                )),
                ast)

        Assert.assertEquals(emptyList<SQLangError>(), errors)
    }

    @Test
    fun invalidToken() {
        val charStream = CharStreams.fromReader(StringReader("#"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(Span(Position(1, 0, 0), 1), error.span)
        Assert.assertTrue("Extraneous input not detected.", error.message?.startsWith("extraneous input '#'") == true)
    }

    @Test
    fun invalidTokenInVariableDeclaration() {
        val charStream = CharStreams.fromReader(StringReader("var \""))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(Span(Position(1, 4, 4), 1), error.span)
        Assert.assertTrue("Mismatched input not detected.", error.message?.startsWith("mismatched input '\"'") == true)
    }

    @Test
    fun unbalancedStringLiteral() {
        val charStream = CharStreams.fromReader(StringReader("print \"Test\nout {3, 6}"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(Span(Position(1, 6, 6), Position(1, 22, 22)), error.span)
        Assert.assertTrue("Mismatched input not detected.", error.message?.startsWith("""mismatched input '"Test\nout {3, 6}'""") == true)
    }

    @Test
    fun outMissingIdentifier() {
        val charStream = CharStreams.fromReader(StringReader("out print \"Test\"\nout {3, 6}"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(Span(Position(1, 4, 4), 5), error.span)
        Assert.assertTrue("Mismatched input not detected.", error.message?.startsWith("mismatched input 'print'") == true)
    }

    @Test
    fun multipleErrors() {
        val charStream = CharStreams.fromReader(StringReader("out print \"Test\"\nout {\"Hello\", 6}"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(2, errors.count())
        val errorA = errors[0]
        Assert.assertEquals(Span(Position(1, 4, 4), 5), errorA.span)
        Assert.assertTrue("Mismatched input not detected.", errorA.message?.startsWith("mismatched input 'print'") == true)
        val errorB = errors[1]
        Assert.assertEquals(Span(Position(2, 5, 22), 7), errorB.span)
        Assert.assertTrue("Mismatched input not detected.", errorB.message?.startsWith("mismatched input '\"Hello\"'") == true)
    }

    @Test
    fun unexpectedEOF() {
        val charStream = CharStreams.fromReader(StringReader(" out \n   "))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        val error = errors[0]
        Assert.assertEquals(Span(Position(1, 1, 1), Position(2, 3, 9)), error.span)
        Assert.assertTrue("Mismatched input not detected.", error.message?.startsWith("mismatched input '<EOF>'") == true)
    }

    @Test
    fun literalTypes() {
        val types = listOf(IntegerLiteral(0), RealLiteral(0.0),
                Sequence(IntegerLiteral(1), IntegerLiteral(2)),
                Lambda(listOf(VarDeclaration("x", Type.Integer)), IntegerLiteral(3)))
                .map(Expression::type)

        Assert.assertEquals(listOf(Type.Integer, Type.Real, Type.Sequence(Type.Integer), Type.Lambda), types)
    }

    @Test
    fun binaryOperationTypes() {
        val expressions = listOf(IntegerLiteral(0), RealLiteral(0.0),
                Sequence(IntegerLiteral(1), IntegerLiteral(2)),
                Lambda(listOf(VarDeclaration("x", Type.Integer)), IntegerLiteral(3)))

        val expected = listOf(
                listOf(Type.Integer, Type.Real,  Type.Error, Type.Error),
                listOf(Type.Real,    Type.Real,  Type.Error, Type.Error),
                listOf(Type.Error,   Type.Error, Type.Error, Type.Error),
                listOf(Type.Error,   Type.Error, Type.Error, Type.Error)
        )

        for (op in listOf(::Sum, ::Sub, ::Mul, ::Div, ::Pow))
            for ((leftIdx,left) in expressions.withIndex()) for ((rightIdx,right) in expressions.withIndex())  {
                Assert.assertEquals(expected[leftIdx][rightIdx], op(left, right).type)
            }
    }

    @Test
    fun variableScope() {
        val charStream = CharStreams.fromReader(StringReader("var i = 5 var j = 8.0 out j out i"))
        val errors = mutableListOf<SQLangError>()

        val program = ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(4, program?.statements?.count())
        val varI = program!!.statements[0] as VarStatement
        val varJ = program  .statements[1] as VarStatement
        val j = (program.statements[2] as OutStatement).expression as VariableRef
        val i = (program.statements[3] as OutStatement).expression as VariableRef
        Assert.assertEquals(Type.Real,    j.type)
        Assert.assertEquals(Type.Integer, i.type)
        Assert.assertTrue("Wrong reference.", i.declaration === varI.declaration)
        Assert.assertTrue("Wrong reference.", j.declaration === varJ.declaration)
    }

    @Test
    fun lambdaScope() {
        val charStream = CharStreams.fromReader(StringReader("var i = 0 out map({0, 10}, i -> i)"))
        val errors = mutableListOf<SQLangError>()

        val program = ASTBuilder.parseStream(charStream, errors)
        Assert.assertEquals(2, program?.statements?.count())
        val funInvoc = (program!!.statements[1] as OutStatement).expression as FunctionInvoc
        Assert.assertEquals(2, funInvoc.args.count())
        val lambda = funInvoc.args[1] as Lambda
        Assert.assertEquals(1, lambda.parameters.count())
        val declI = lambda.parameters[0]
        val i = lambda.body as VariableRef
        Assert.assertEquals(Type.Integer, i.type)
        Assert.assertTrue("Wrong reference.", i.declaration === declI)
    }

    @Test
    fun functionScope() {
        val charStream = CharStreams.fromReader(StringReader("var n = 10 var sequence = map({0, n}, i -> (-1)^i / (2 * i + 1))\n" +
                "var quarterPi = reduce(sequence, 0, x y -> x + y)"))
        val errors = mutableListOf<SQLangError>()

        val program = ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(3, program?.statements?.count())
        val varSequence = program!!.statements[1] as VarStatement
        val varQuarterPi = program .statements[2] as VarStatement
        val map    = varSequence.expression  as FunctionInvoc
        val reduce = varQuarterPi.expression as FunctionInvoc
        Assert.assertEquals(Type.Sequence(Type.Integer), map.type)
        Assert.assertEquals(Type.Integer, reduce.type)
        Assert.assertTrue("Wrong reference.", map.target    is MapFunction)
        Assert.assertTrue("Wrong reference.", reduce.target is ReduceFunction)
    }

    @Test
    fun functionScopeRealSequence() {
        val charStream = CharStreams.fromReader(StringReader("var n = 10 var sequence = map({0, n}, i -> i*1.125)\n" +
                "var quarterPi = reduce(sequence, 0, x y -> x + y)"))
        val errors = mutableListOf<SQLangError>()

        val program = ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(3, program?.statements?.count())
        val varSequence = program!!.statements[1] as VarStatement
        val varQuarterPi = program .statements[2] as VarStatement
        val map    = varSequence.expression  as FunctionInvoc
        val reduce = varQuarterPi.expression as FunctionInvoc
        Assert.assertEquals(Type.Sequence(Type.Real), map.type)
        Assert.assertEquals(Type.Real, reduce.type)
        Assert.assertTrue("Wrong reference.", map.target    is MapFunction)
        Assert.assertTrue("Wrong reference.", reduce.target is ReduceFunction)
    }

    @Test
    fun undeclaredVariable() {
        val charStream = CharStreams.fromReader(StringReader("out a"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        Assert.assertTrue("Missing variable not detected.", errors[0].message!!.startsWith("Unknown variable 'a'"))
    }

    @Test
    fun redeclaredVariable() {
        val charStream = CharStreams.fromReader(StringReader("var a = 1 var a = 2"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        Assert.assertTrue("Redeclaration of variable not detected.", errors[0].message!!.startsWith("Variable 'a' redeclared"))
    }

    @Test
    fun incompatibleBinOp() {
        for (program in listOf("out 1+{3,5}", "out 1*{3,5}", "out 1^{3,5}")) {
            val charStream = CharStreams.fromReader(StringReader(program))
            val errors = mutableListOf<SQLangError>()

            ASTBuilder.parseStream(charStream, errors)

            Assert.assertEquals(1, errors.count())
            Assert.assertTrue("Incompatible arguments not detected.", errors[0].message!!.startsWith("Incompatible arguments Integer and Sequence"))
        }
    }

    @Test
    fun badSequence() {
        for ((case, program) in mapOf(Pair("start", "out {3.0,5}"), Pair("end", "out {3,5.0}"))) {
            val charStream = CharStreams.fromReader(StringReader(program))
            val errors = mutableListOf<SQLangError>()

            ASTBuilder.parseStream(charStream, errors)

            Assert.assertEquals(1, errors.count())
            Assert.assertTrue("Bad sequence not detected.", errors[0].message!!.startsWith("Illegal sequence $case, expected Integer, found Real"))
        }
    }

    @Test
    fun undefinedFunction() {
        for ((case, input) in mapOf(Pair("map(…, …, …)", "out map({3,5}, i -> i * 2.0, i -> i / 4)"),
                Pair("reduce(…, …, …, …)", "out reduce({3,5}, 2.0, 4.2, x y -> x + y)"))) {
            val charStream = CharStreams.fromReader(StringReader(input))
            val errors = mutableListOf<SQLangError>()

            ASTBuilder.parseStream(charStream, errors)

            Assert.assertEquals(1, errors.count())
            Assert.assertTrue("Undefined function not detected.", errors[0].message!!.startsWith("Function $case is not defined"))
        }
    }

    @Test
    fun badFunctionArgTypes() {
        for ((case, input) in mapOf(Pair("map(Real, Lambda)", "out map(3.0, i -> i * 2.0)"),
                Pair("reduce(Sequence<Integer>, Real, Sequence<Integer>)", "out reduce({3,5}, 2.0, {3,5})"))) {
            val charStream = CharStreams.fromReader(StringReader(input))
            val errors = mutableListOf<SQLangError>()

            ASTBuilder.parseStream(charStream, errors)

            Assert.assertEquals(1, errors.count())
            Assert.assertTrue("Undefined function not detected.", errors[0].message!!.startsWith("Incompatible arguments $case for function"))
        }
    }

    @Test
    fun hugeInteger() {
        val charStream = CharStreams.fromReader(StringReader("out 1000000000000000000000000000000000"))
        val errors = mutableListOf<SQLangError>()

        ASTBuilder.parseStream(charStream, errors)

        Assert.assertEquals(1, errors.count())
        Assert.assertTrue("Overflow not detected.", errors[0].message!!.startsWith("Number is not within value range"))
    }

    @Test
    fun nodeTrace() {
        val charStream = CharStreams.fromReader(StringReader("""var n = 500
var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))
out reduce(sequence, 0, x y -> x - y)
 print "Hello""""))
        val errors = mutableListOf<SQLangError>()

        val tree = ASTBuilder.parseStream(charStream, errors)
        tree!!.accept(object: CountingVisitor() {
            override fun visit(program: Program) {
                Assert.assertEquals(Span(Position(1, 0, 0), Position(4, 14, 120)), program.span)
                Assert.assertEquals(1, ++visitCounter)
                program.acceptChildren(this)
            }

            override fun visit(varStatement: VarStatement) {
                when {
                    varStatement.declaration.identifier == "n" -> {
                        Assert.assertEquals(Span(Position(1, 0, 0),
                                Position(1, 11, 11)), varStatement.span)
                        Assert.assertEquals(2, ++visitCounter)
                    }
                    else -> {
                        Assert.assertEquals(Span(Position(2, 0, 12), Position(2, 55, 67)),
                                varStatement.span)
                        Assert.assertEquals(3, ++visitCounter)
                    }
                }
                varStatement.acceptChildren(this)
            }

            override fun visit(varDeclaration: VarDeclaration) {
                when(varDeclaration.identifier) {
                    "sequence" -> {
                        Assert.assertEquals(Span(Position(2, 4, 16), Position(2, 12, 24)),
                                varDeclaration.span)
                        Assert.assertEquals(4, ++visitCounter)
                    }
                    "x" -> {
                        Assert.assertEquals(Span(Position(3, 24, 92), Position(3, 25, 93)),
                                varDeclaration.span)
                        Assert.assertEquals(17, ++visitCounter)
                    }
                }
            }

            override fun visit(outStatement: OutStatement) {
                Assert.assertEquals(Span(Position(3, 0, 68), Position(3, 37, 105)),
                        outStatement.span)
                Assert.assertEquals(13, ++visitCounter)
                outStatement.acceptChildren(this)
            }

            override fun visit(functionInvoc: FunctionInvoc) {
                when (functionInvoc.identifier) {
                    "map" -> {
                        Assert.assertEquals(Span(Position(2, 15, 27), Position(2, 55, 67)),
                                functionInvoc.span)
                        Assert.assertEquals(5, ++visitCounter)
                    }
                    "reduce" -> {
                        Assert.assertEquals(Span(Position(3, 4, 72), Position(3, 37, 105)),
                                functionInvoc.span)
                        Assert.assertEquals(14, ++visitCounter)
                    }
                }
                functionInvoc.acceptChildren(this)
            }

            override fun visit(lambda: Lambda) {
                if (lambda.parameters[0].identifier == "x") {
                    Assert.assertEquals(Span(Position(3, 24, 92), Position(3, 36, 104)),
                            lambda.span)
                    Assert.assertEquals(16, ++visitCounter)
                }
                lambda.acceptChildren(this)
            }

            override fun visit(printStatement: PrintStatement) {
                Assert.assertEquals(Span(Position(4, 1, 107), Position(4, 14, 120)),
                        printStatement.span)
                Assert.assertEquals(19, ++visitCounter)
            }

            override fun visit(variableRef: VariableRef) {
                if (variableRef.identifier == "sequence") {
                    Assert.assertEquals(Span(Position(3, 11, 79), Position(3, 19, 87)),
                            variableRef.span)
                    Assert.assertEquals(15, ++visitCounter)
                }
            }

            override fun visit(sum: Sum) {
                Assert.assertEquals(Span(Position(2, 41, 53), Position(2, 54, 66)),
                        sum.span)
                Assert.assertEquals(10, ++visitCounter)
                sum.acceptChildren(this)
            }

            override fun visit(sub: Sub) {
                Assert.assertEquals(Span(Position(3, 31, 99), Position(3, 36, 104)),
                        sub.span)
                Assert.assertEquals(18, ++visitCounter)
            }

            override fun visit(mul: Mul) {
                Assert.assertEquals(Span(Position(2, 42, 54), Position(2, 49, 61)),
                        mul.span)
                Assert.assertEquals(11, ++visitCounter)
                mul.acceptChildren(this)
            }

            override fun visit(div: Div) {
                Assert.assertEquals(Span(Position(2, 32, 44), Position(2, 54, 66)),
                        div.span)
                Assert.assertEquals(7, ++visitCounter)
                div.acceptChildren(this)
            }

            override fun visit(pow: Pow) {
                Assert.assertEquals(Span(Position(2, 32, 44), Position(2, 38, 50)),
                        pow.span)
                Assert.assertEquals(8, ++visitCounter)
                pow.acceptChildren(this)
            }

            override fun visit(sequence: Sequence) {
                Assert.assertEquals(Span(Position(2, 19, 31), Position(2, 25, 37)),
                        sequence.span)
                Assert.assertEquals(6, ++visitCounter)
            }

            override fun visit(realLiteral: RealLiteral) {
                Assert.assertEquals(Span(Position(2, 42, 54), Position(2, 45, 57)),
                        realLiteral.span)
                Assert.assertEquals(12, ++visitCounter)
            }

            override fun visit(integerLiteral: IntegerLiteral) {
                if (integerLiteral.value < 0) {
                    Assert.assertEquals(Span(Position(2, 32, 44), Position(2, 36, 48)),
                            integerLiteral.span)
                    Assert.assertEquals(9, ++visitCounter)
                }
            }
        })
    }
}