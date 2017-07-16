package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.Statement


open class CountingParticipant: InterpreterParticipant {
    var statementCounter = 0

    override fun statementExecuting(statement: Statement) {
        statementCounter++
    }
}