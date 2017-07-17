package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.document.text
import com.fkistner.SouthQuay.parser.*
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

        override fun output(statement: Statement, string: String) = checkStateAndSwingInvokeLater {
            editor.outputTextArea.append("$string\n")
        }

        override fun statusInfo(statusInfo: String) = checkStateAndSwingInvokeLater {
            editor.statusLabel.text = statusInfo
        }

        override fun error(errors: List<SQLangError>) = checkStateAndSwingInvokeLater {
            errors.forEach { editor.outputTextArea.append("ERROR: $it\n") }
        }

        override fun exception(message: String) = checkStateAndSwingInvokeLater {
            editor.outputTextArea.append("EXCEPTION: $message\n")
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