package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.parserForString
import com.fkistner.SouthQuay.interpreter.functions.*
import org.antlr.v4.runtime.CharStreams
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
                    VarStatement(VarDeclaration("myVar1", Type.Sequence),
                            Sequence(IntegerLiteral(3), IntegerLiteral(10)))
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
                    VarStatement(VarDeclaration("i", Type.Sequence), FunctionInvoc("map", listOf(
                            Sequence(IntegerLiteral(1), IntegerLiteral(4)),
                            Lambda(listOf(VarDeclaration("n", Type.Integer)), Mul(IntegerLiteral(2), VariableRef("n")))
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

    @Test
    fun literalTypes() {
        val types = listOf(IntegerLiteral(0), RealLiteral(0.0),
                Sequence(IntegerLiteral(1), IntegerLiteral(2)),
                Lambda(listOf(VarDeclaration("x", Type.Integer)), IntegerLiteral(3)))
                .map(Expression::type)

        Assert.assertEquals(listOf(Type.Integer, Type.Real, Type.Sequence, Type.Lambda), types)
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
        Assert.assertEquals(Type.Sequence, map.type)
        Assert.assertEquals(Type.Integer,  reduce.type)
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
        for ((case, input) in mapOf(Pair("map(Sequence, Lambda, Lambda)", "out map({3,5}, i -> i * 2.0, i -> i / 4)"),
                Pair("reduce(Sequence, Real, Real, Lambda)", "out reduce({3,5}, 2.0, 4.2, x y -> x + y)"))) {
            val charStream = CharStreams.fromReader(StringReader(input))
            val errors = mutableListOf<SQLangError>()

            ASTBuilder.parseStream(charStream, errors)

            Assert.assertEquals(1, errors.count())
            Assert.assertTrue("Undefined function not detected.", errors[0].message!!.startsWith("Function $case is not defined"))
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
}