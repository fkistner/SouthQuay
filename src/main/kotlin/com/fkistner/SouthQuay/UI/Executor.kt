package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.interpreter.Interpreter
import com.fkistner.SouthQuay.measurePerformance
import com.fkistner.SouthQuay.parser.*
import java.io.*
import java.util.concurrent.*


class Executor(val executionParticipant: ExecutionParticipant) {
    val executorService = Executors.newSingleThreadExecutor()!!

    fun start(text: String): CompletableFuture<Void> {
        val interpreter = Interpreter(executionParticipant)
        return CompletableFuture.runAsync(Runnable { execute(interpreter, text) }, executorService)
    }

    private fun execute(interpreter: Interpreter, text: String) {
        try {
            var status: String
            var errors: List<SQLangError> = emptyList()
            var program: Program? = null

            try {
                val parseDuration = with(measurePerformance(text, ASTBuilder::parseText)) {
                    with(first) { program = first; errors = second }
                    second
                }
                status = "Parse time %.4fs".format(parseDuration)
                executionParticipant.statusInfo(status)
            } finally {
                if (errors.isNotEmpty()) executionParticipant.error(errors)
            }

            program?.let {
                val runtimeErrors = mutableListOf<SQLangError>()
                try {
                    val executionDuration = measurePerformance(it) { interpreter.execute(it, runtimeErrors) }
                    status += ", execution time %.4fs".format(executionDuration)
                    executionParticipant.statusInfo(status)
                } finally {
                    if (runtimeErrors.isNotEmpty()) executionParticipant.error(runtimeErrors)
                }
            }
        } catch (t: Throwable) {
            val writer = StringWriter()
            t.printStackTrace(PrintWriter(writer))
            executionParticipant.exception(writer.buffer.toString())
        }
    }
}