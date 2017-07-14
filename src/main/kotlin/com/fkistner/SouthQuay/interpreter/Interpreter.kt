package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*

class Interpreter(val executionParticipant: ExecutionParticipant) {
    private val statementVisitor = object: ASTVisitor<Unit> {
        override fun visit(program: Program) {
            program.acceptChildren(this)
        }

        override fun visit(printStatement: PrintStatement) {
            executionParticipant.statementExecuting(printStatement)
            executionParticipant.output(printStatement, printStatement.stringLiteral)
        }

        override fun visit(outStatement: OutStatement) {
            executionParticipant.statementExecuting(outStatement)
            val expressionVisitor = ExpressionVisitor()
            val result = outStatement.expression.accept(expressionVisitor)
            executionParticipant.output(outStatement, result.toString())
        }
    }

    fun execute(program: Program) {
        program.accept(statementVisitor)
    }
}