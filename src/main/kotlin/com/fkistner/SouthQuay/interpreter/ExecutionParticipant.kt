package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*

/**
 * Interface for an observer that participates in the script execution.
 */
interface ExecutionParticipant {
    /**
     * Notifies about elapsed time during parsing.
     * @param parseTime Elapsed time in seconds
     */
    fun parseTimeDiagnostics(parseTime: Double) = Unit

    /**
     * Notifies about elapsed time during execution.
     * @param runtime Elapsed time in seconds
     */
    fun runtimeDiagnostics(runtime: Double) = Unit

    /**
     * Notifies about start of statement execution.
     * @param statement Statement starting to execute
     */
    fun statementExecuting(statement: Statement) = Unit

    /**
     * Notifies about script output.
     * @param statement Statement responsible for output
     * @param string Text output
     */
    fun output(statement: Statement, string: String) = Unit

    /**
     * Notifies about a new active value.
     * @param declaration Declaration that identifies value
     * @param value Value
     */
    fun newValue(declaration: VarDeclaration, value: Any?) = Unit

    /**
     * Notifies about new errors encountered during parsing or execution.
     * @param errors List of errors encountered
     */
    fun error(errors: List<SQLangError>) = Unit

    /**
     * Notifies about an unexpected, fatal exception.
     * @param message Exception message
     */
    fun exception(throwable: Throwable) = Unit
}
