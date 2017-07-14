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
    }

    fun execute(program: Program) {
        program.accept(statementVisitor)
    }
}