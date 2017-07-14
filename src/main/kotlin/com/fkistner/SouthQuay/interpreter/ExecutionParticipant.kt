package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.Statement


interface ExecutionParticipant {
    fun output(statement: Statement, string: String) {}
    fun statementExecuting(statement: Statement) {}
}