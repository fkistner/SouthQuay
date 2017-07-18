package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.document.text
import com.fkistner.SouthQuay.parser.*
import java.awt.Color
import java.util.*
import java.util.concurrent.CancellationException
import javax.swing.SwingUtilities
import javax.swing.text.*


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

        val lines = TreeMap<Int, Pair<String, AttributeSet?>>()

        fun Color.asForeground(): AttributeSet {
            return StyleContext.getDefaultStyleContext()
                    .addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, this)
        }

        fun updateOutput() {
            var lastLine = 1
            val styledDocument = editor.outputTextArea.styledDocument
            styledDocument.remove(0, styledDocument.length)
            for ((line, pair) in lines) {
                val (message, attributes) = pair
                styledDocument.insertString(styledDocument.length, "\n".repeat(line - lastLine), null)
                styledDocument.insertString(styledDocument.length, message, attributes)
                lastLine = line
            }
        }

        fun prepareOutput(span: Span, string: String, color: Color? = null) {
            val line = span.start.line
            if (line !in lines) lines.put(line, string.to(color?.asForeground()))
        }

        override fun output(statement: Statement, string: String) = checkStateAndSwingInvokeLater {
            prepareOutput(statement.span, string)
            updateOutput()
        }

        override fun newValue(declaration: VarDeclaration, string: String) = checkStateAndSwingInvokeLater {
            prepareOutput(declaration.span, "${declaration.identifier} = $string", SyntaxColors.SecondaryColor)
            updateOutput()
        }

        override fun statusInfo(statusInfo: String) = checkStateAndSwingInvokeLater {
            editor.statusLabel.text = statusInfo
        }

        override fun error(errors: List<SQLangError>) = checkStateAndSwingInvokeLater {
            editor.parserAdapter.additionalErrors.addAll(errors)
            errors.forEach { prepareOutput(it.span, "ERROR: ${it.message}", SyntaxColors.ErrorColor) }
            updateOutput()
        }

        override fun exception(message: String) = checkStateAndSwingInvokeLater {
            val styledDocument = editor.outputTextArea.styledDocument
            styledDocument.remove(0, styledDocument.length)
            styledDocument.insertString(styledDocument.length, "EXCEPTION: $message", SyntaxColors.ErrorColor.asForeground())
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
                parserAdapter.additionalErrors.clear()
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