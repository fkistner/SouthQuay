package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.document.text
import com.fkistner.SouthQuay.parser.*
import java.util.*
import java.util.concurrent.CancellationException
import javax.swing.SwingUtilities


class ExecutionControl(val editor: Editor): ExecutionState<Unit> {
    interface State: ExecutionState<State>

    private val idle = IdleState()
    @Volatile private var state: State = idle

    override fun run()    { state = state.run() }
    override fun abort()  { state = state.abort() }
    override fun finish() { state = state.finish() }

    private inner class RunningState(programText: String): ExecutionControl.State, ExecutionParticipant {
        val future = Executor(this).start(programText)
        init {
            future.thenRun { SwingUtilities.invokeLater(this@ExecutionControl::finish) }
        }

        var rerun = false

        fun checkState() { if (state != this) throw CancellationException() }
        fun checkStateAndSwingInvokeLater(action: () -> Unit) {
            checkState()
            SwingUtilities.invokeLater(action)
        }

        val lines = TreeMap<Int, String>()

        fun updateOutput() {
            var buffer = ""
            var lastLine = 1
            for ((line,message) in lines) {
                buffer += "\n".repeat(line - lastLine)
                buffer += message
                lastLine = line
            }
            editor.outputTextArea.text = buffer
            editor.outputTextArea.revalidate()
        }

        fun prepareOutput(span: Span, string: String) {
            val line = span.start.line
            if (line !in lines) lines.put(line, string)
        }

        override fun output(statement: Statement, string: String) = checkStateAndSwingInvokeLater {
            prepareOutput(statement.span, string)
            updateOutput()
        }

        override fun newValue(declaration: VarDeclaration, string: String) = checkStateAndSwingInvokeLater {
            prepareOutput(declaration.span, "${declaration.identifier} = $string")
            updateOutput()
        }

        override fun statusInfo(statusInfo: String) = checkStateAndSwingInvokeLater {
            editor.statusLabel.text = statusInfo
        }

        override fun error(errors: List<SQLangError>) = checkStateAndSwingInvokeLater {
            ParserAdapter.additionalErrors.addAll(errors)
            errors.forEach { prepareOutput(it.span, "ERROR: ${it.message}") }
            updateOutput()
        }

        override fun exception(message: String) = checkStateAndSwingInvokeLater {
            editor.outputTextArea.text = "EXCEPTION: $message"
        }

        override fun statementExecuting(statement: Statement) = checkState()

        override fun run() = this.also { it.rerun = true }
        override fun abort(): IdleState {
            future.cancel(true)
            return complete()
        }

        override fun finish() = complete().let {
            when {
                rerun -> it.run()
                else -> it
            }
        }

        private fun complete(): IdleState {
            with(editor) {
                evaluateButton.isVisible = true
                abortButton.isVisible = false
            }
            return idle
        }
    }

    private inner class IdleState: ExecutionControl.State {
        override fun run(): RunningState {
            ParserAdapter.additionalErrors.clear()
            with(editor) {
                evaluateButton.isVisible = false
                abortButton.isVisible = true
                outputTextArea.text = ""
                statusLabel.text = ""

                return RunningState(documentModel.text)
            }
        }

        override fun abort()  = throw IllegalStateException()
        override fun finish() = this
    }
}