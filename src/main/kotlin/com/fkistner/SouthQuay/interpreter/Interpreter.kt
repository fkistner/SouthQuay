package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*


class Interpreter(val executionParticipant: ExecutionParticipant) {

    val visitor = object: ASTVisitor {
        override fun visit(printStatement: PrintStatement): Boolean {
            executionParticipant.statementExecuting(printStatement)
            executionParticipant.output(printStatement, printStatement.stringLiteral)
            return true
        }
    }

    fun execute(program: Program) {
        program.accept(visitor)
    }
}