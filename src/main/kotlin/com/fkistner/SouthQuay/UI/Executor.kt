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
        var status: String = ""
        var errors: List<SQLangError> = emptyList()
        var parseDuration: Double? = null
        var program: Program? = null


        try {
            with(measurePerformance(text, ASTBuilder::parseText)) {
                with(first) { program = first; errors = second }
                parseDuration = second
            }
        } finally {
            parseDuration?.let {
                status = "Parse time %.4fs".format(it)
                executionParticipant.statusInfo(status)
            }
            if (errors.isNotEmpty()) executionParticipant.error(errors)
        }

        var executionDuration: Double? = null

        try {
            executionDuration = program?.let { measurePerformance(it, interpreter::execute).second }
        } catch (t: Throwable) {
            val writer = StringWriter()
            t.printStackTrace(PrintWriter(writer))
            executionParticipant.exception(writer.buffer.toString())
        } finally {
            executionDuration?.let {
                status += ", execution time %.4fs".format(it)
                executionParticipant.statusInfo(status)
            }
        }
    }
}