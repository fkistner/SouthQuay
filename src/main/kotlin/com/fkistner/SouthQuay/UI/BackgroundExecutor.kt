package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.interpreter.*
import com.fkistner.SouthQuay.measurePerformance
import com.fkistner.SouthQuay.parser.*
import java.util.concurrent.*

/**
 * Provides background execution of the interpreter using a worker thread.
 * @param participant Observer of the script execution
 */
class BackgroundExecutor(val participant: ExecutionParticipant) {
    /**
     * Schedules the provided script for parsing and interpretation on a worker thread.
     * @param text Program text to be interpreted
     */
    fun start(text: String): CompletableFuture<Void> {
        val pool = ForkJoinPool()
        val interpreter = StatementInterpreter(participant)
        return CompletableFuture.runAsync(Runnable { execute(interpreter, text) }, pool).also {
            it.whenComplete { _,_ -> pool.shutdownNow() }
        }
    }

    /**
     * Parser the program text and executes it with the interpreter providing
     * status and performance information to the [participant].
     * @param interpreter Interpreter to execute the script with
     * @param text Program text to be interpreted
     */
    private fun execute(interpreter: StatementInterpreter, text: String) {
        try {
            var errors: List<SQLangError> = emptyList()
            var program: Program? = null

            try {
                val parseDuration = with(measurePerformance(text, ASTBuilder::parseText)) {
                    with(first) { program = first; errors = second }
                    second
                }
                participant.parseTimeDiagnostics(parseDuration)
                if (errors.isNotEmpty()) return
            } finally {
                if (errors.isNotEmpty()) participant.error(errors)
            }

            program?.let {
                val runtimeErrors = mutableListOf<SQLangError>()
                try {
                    val executionDuration = measurePerformance(it) { interpreter.execute(it, runtimeErrors) }
                    participant.runtimeDiagnostics(executionDuration)
                } finally {
                    if (runtimeErrors.isNotEmpty()) participant.error(runtimeErrors)
                }
            }
        } catch (t: Throwable) {
            participant.exception(t)
        }
    }
}