package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.document.text
import com.fkistner.SouthQuay.interpreter.*
import com.fkistner.SouthQuay.parser.*
import java.awt.Color
import java.io.*
import java.util.*
import java.util.concurrent.CancellationException
import javax.swing.SwingUtilities
import javax.swing.text.*

/**
 * State machine for the script execution.
 *
 * There are two states [RunningState] and [IdleState] (initial), and three stimuli [run], [abort], [finish].
 *
 * @param editor Editor to control
 */
class ExecutionControl(val editor: Editor): ExecutionState<Unit> {
    /** State of the script execution providing transitions to a new state upon stimuli. */
    interface State: ExecutionState<State>

    /** Idle state instance. */
    private val idle = IdleState()

    /** Current state. Initially idle. */
    private var state: State = idle

    /** Signals to start the execution of the current script and stores new state. */
    override fun run()    { state = state.run() }
    /** Signals to abort the execution of the current script and stores new state. */
    override fun abort()  { state = state.abort() }
    /** Signals that the execution of the current script has finished and stores new state. */
    override fun finish() { state = state.finish() }

    /**
     * State object for running a script.
     *
     * Starts the execution of the script and responds to runtime events.
     * @param programText Text of the script to execute
     */
    private inner class RunningState(programText: String): ExecutionControl.State, ExecutionParticipant {
        /** Future of the current execution. */
        val future = BackgroundExecutor(this).start(programText)
        init {
            // Register finish stimuli upon completion (not executed on cancel).
            future.thenRun {
                SwingUtilities.invokeLater(this@ExecutionControl::finish)
            }
        }

        /** Signals if [run] stimuli were received during execution,
         * which will trigger a state change back to running upon [finish]. */
        var rerun = false


        //region State Changes

        /**
         * Marks execution for [rerun] upon [finish]. Does not transition state.
         * @return [RunningState]
         */
        override fun run() = this.also { it.rerun = true }

        /**
         * Cancels execution, updates UI, and transitions to idle state.
         * @return [IdleState]
         */
        override fun abort(): IdleState {
            future.cancel(true)
            return complete()
        }

        /**
         * Updates UI and transitions to idle state. If [rerun] is requested, automatically triggers another run state.
         * @return [IdleState] or [RunningState]
         */
        override fun finish() = complete().let {
            when {
                rerun -> it.run()
                else -> it
            }
        }

        /**
         * Executes common UI updates as part of the [abort] and [finish] exit actions.
         * @return Idle state
         */
        private fun complete(): IdleState {
            with(editor) {
                evaluateButton.isVisible = true
                abortButton.isVisible = false
            }
            return idle
        }

        //endregion


        //region Interpreter Participant

        /** Checks whether this is still the current state.
         * @throws CancellationException Thrown if this state is stale
         */
        fun checkState() { if (future.isCancelled) throw CancellationException() }

        /**
         * Checks whether this is still the current state and invokes the [action] on the Swing UI thread.
         * @param action Action to be executed on the Swing UI thread
         * @throws CancellationException Thrown if this state is stale
         * @see checkState
         * @see SwingUtilities.invokeLater
         */
        fun checkStateAndSwingInvokeLater(action: () -> Unit) {
            checkState()
            SwingUtilities.invokeLater(action)
        }

        /**
         * Lines to be output into [Editor.outputTextPane] upon call to [updateOutput].
         *
         * [Map.keys] are the line numbers, [Map.values] are the text for each line
         * optionally in combination with style information.
         */
        val outputLines = TreeMap<Int, Pair<String, AttributeSet?>>()

        /** Creates an attribute set for text display from this color.
         * @return Attribute set with this color set as foreground color
         */
        fun Color.asForeground(): AttributeSet {
            return StyleContext.getDefaultStyleContext()
                    .addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, this)
        }

        /**
         * Updates the [Editor.outputTextPane] with the collected [outputLines].
         */
        fun updateOutput() {
            var lastLine = 1
            val styledDocument = editor.outputTextPane.styledDocument
            styledDocument.remove(0, styledDocument.length)
            for ((line, pair) in outputLines) {
                val (message, attributes) = pair
                styledDocument.insertString(styledDocument.length, "\n".repeat(line - lastLine), null)
                styledDocument.insertString(styledDocument.length, message, attributes)
                lastLine = line
            }
            editor.outputTextPane.invalidate()
        }

        /**
         * Adds to the [outputLines].
         * @param span Source of the output
         * @param string Text to output
         * @param color Color the text should appear in
         * */
        fun addToOutput(span: Span, string: String, color: Color? = null) {
            val line = span.start.line
            if (line !in outputLines) outputLines.put(line, string.to(color?.asForeground()))
        }

        override fun output(statement: Statement, string: String) = checkStateAndSwingInvokeLater {
            addToOutput(statement.span, string)
            updateOutput()
        }

        override fun newValue(declaration: VarDeclaration, value: Any?) = checkStateAndSwingInvokeLater {
            addToOutput(declaration.span, "${declaration.identifier} = $value", SyntaxColors.SecondaryColor)
            updateOutput()
        }

        override fun parseTimeDiagnostics(parseTime: Double) = checkStateAndSwingInvokeLater {
            editor.statusLabel.text = "Parse time %.4fs".format(parseTime)

        }

        override fun runtimeDiagnostics(runtime: Double) = checkStateAndSwingInvokeLater {
            editor.statusLabel.text += ", execution time %.4fs".format(runtime)
        }

        override fun error(errors: List<SQLangError>) = checkStateAndSwingInvokeLater {
            editor.parserAdapter.additionalErrors.addAll(errors)
            errors.forEach { addToOutput(it.span, "ERROR: ${it.message}", SyntaxColors.ErrorColor) }
            updateOutput()
        }

        /**
         * Extracts the complete exception details.
         * @return Stack trace as string
         */
        fun Throwable.toFullString(): String {
            val writer = StringWriter()
            printStackTrace(PrintWriter(writer))
            return writer.buffer.toString()
        }

        override fun exception(throwable: Throwable) {
            val message = throwable.toFullString()
            checkStateAndSwingInvokeLater {
                val styledDocument = editor.outputTextPane.styledDocument
                styledDocument.remove(0, styledDocument.length)
                styledDocument.insertString(styledDocument.length, "EXCEPTION: $message", SyntaxColors.ErrorColor.asForeground())
            }
        }

        override fun statementExecuting(statement: Statement) = checkState()

        //endregion
    }

    /**
     * State object for idle.
     */
    private inner class IdleState: ExecutionControl.State {
        //region State Changes

        /**
         * Updates UI and transitions into running state.
         * @return [RunningState]
         */
        override fun run(): RunningState {
            with(editor) {
                parserAdapter.additionalErrors.clear()
                evaluateButton.isVisible = false
                abortButton.isVisible = true
                outputTextPane.text = ""
                statusLabel.text = ""

                return RunningState(documentModel.text)
            }
        }

        override fun abort()  = this
        override fun finish() = this

        //endregion
    }
}
