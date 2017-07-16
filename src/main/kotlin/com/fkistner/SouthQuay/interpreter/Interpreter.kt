package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*

class Interpreter(val executionParticipant: InterpreterParticipant) {

    private val statementVisitor = object: ASTVisitor<Unit> {
        var context = ExecutionContext()

        override fun visit(program: Program) {
            program.acceptChildren(this)
        }

        override fun visit(printStatement: PrintStatement) {
            executionParticipant.statementExecuting(printStatement)
            executionParticipant.output(printStatement, printStatement.stringLiteral)
        }

        override fun visit(outStatement: OutStatement) {
            executionParticipant.statementExecuting(outStatement)
            val expressionVisitor = ExpressionVisitor(context)
            val result = outStatement.expression.accept(expressionVisitor)
            executionParticipant.output(outStatement, result.toString())
        }

        override fun visit(varStatement: VarStatement) {
            executionParticipant.statementExecuting(varStatement)
            val expressionVisitor = ExpressionVisitor(context)
            val result = varStatement.expression.accept(expressionVisitor)
            context.activeValues[varStatement.declaration] = result
        }
    }

    fun execute(program: Program) {
        program.accept(statementVisitor)
    }
}