package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.interpreter.InterpreterParticipant
import com.fkistner.SouthQuay.parser.SQLangError


interface ExecutionParticipant: InterpreterParticipant {
    fun statusInfo(statusInfo: String) = Unit
    fun error(errors: List<SQLangError>) = Unit
    fun exception(message: String) = Unit
}
