package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*

interface InterpreterParticipant {
    fun output(statement: Statement, string: String) = Unit
    fun statementExecuting(statement: Statement) = Unit
}
