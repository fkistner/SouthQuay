package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.ApplicationName
import com.fkistner.SouthQuay.document.*
import org.fife.ui.autocomplete.AutoCompletion
import java.awt.Window
import java.awt.event.*
import java.net.URL
import javax.swing.JFrame

/**
 * Main editor view controller that manages editing and execution of a script document.
 * Connects all functionality to the UI.
 *
 * Each editor window is managed by a unique instance.
 * @param path Path to document that should be opened upon display
 */
class Editor(path: URL? = null): EditorBase(), DocumentModelListener, MenuListener {
    /** UI frame with menu bar provided by [Menu] */
    val frame = JFrame(ApplicationName).also { it.jMenuBar = Menu.create(this) }
    /** File [Dialogs] controller.  */
    val dialog = Dialogs(frame)

    /** Adapter providing code validation for the [syntaxTextArea]. */
    val parserAdapter = ParserAdapter()
    /** Auto completion controller fed by [CompletionProposalGenerator]. */
    val autoCompletion = AutoCompletion(CompletionProposalGenerator().also {
        it.setAutoActivationRules(true, " ,=({")
    })

    init {
        evaluateButton.addActionListener { executionControl.run() }
        abortButton.addActionListener    { executionControl.abort() }

        syntaxTextArea.addParser(parserAdapter)
        syntaxTextArea.syntaxScheme = SyntaxColors
        autoCompletion.autoCompleteSingleChoices = false
        outputTextPane.editorKit = OutputEditorKit

        frame.addWindowListener(object: WindowAdapter() {
            override fun windowClosing(e: WindowEvent) = fileClose()
        })
        frame.contentPane = rootPanel
        frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }

    /** Document model, which maintains the buffer state. */
    val documentModel = DocumentModel(path, this)
    /** State machine, which manages the different UI states in regards to the script execution.  */
    val executionControl = ExecutionControl(this)

    /**
     * Saves the file if a file path is available or falls back to [trySaveAs].
     * @return `true` if the file was saved
     */
    private fun trySave() = save(documentModel.path) || trySaveAs()

    /**
     * Asks user for file path and saves the file if the user provided one.
     * @return `true` if the file was saved
     */
    private fun trySaveAs() = save(dialog.saveFile()?.let(documentModel::adaptPath))

    /**
     * Saves file if file path was provided
     * @return `true` if the file was saved
     */
    private fun save(file: URL?) = file?.let { documentModel.save(it) } != null


    //region Menu Listener

    override fun fileNew() { Editor() }
    override fun fileOpen() {
        dialog.openFile()?.let { fromURL ->
            if (documentModel.path == null && !documentModel.isDirty)
                documentModel.open(fromURL)
            else
                Editor(fromURL)
        }
    }

    override fun fileClose() {
        if (documentModel.isDirty) {
            when (dialog.shouldSaveFile(documentModel.documentName)) {
                true -> if (!trySave()) return
                null -> return
                false -> documentModel.close()
            }
        }
        executionControl.abort()
        frame.isVisible = false
        frame.dispose()
        if (Window.getWindows().none { it.isVisible }) System.exit(0)
    }

    override fun fileSave() { trySave() }
    override fun fileSaveAs() { trySaveAs() }

    //endregion

    //region Document Model Listener

    override fun newDocument(documentModel: DocumentModel) {
        syntaxTextArea.document = documentModel.document
        syntaxTextArea.forceReparsing(parserAdapter)
        documentModel.document.setSyntaxStyle(SyntaxTokenAdapter)

        autoCompletion.uninstall()
        autoCompletion.install(syntaxTextArea)
        autoCompletion.isAutoActivationEnabled = true

        infoChanged(documentModel)
    }

    override fun infoChanged(documentModel: DocumentModel) {
        var windowTitle = "$ApplicationName – ${documentModel.documentName}"
        if (documentModel.isDirty) windowTitle += "*"
        frame.title = windowTitle
        frame.rootPane.putClientProperty("Window.documentModified", documentModel.isDirty)
    }

    override fun textChanged(documentModel: DocumentModel) = executionControl.run()

    //endregion
}