package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.*
import com.fkistner.SouthQuay.document.*
import org.fife.ui.autocomplete.AutoCompletion
import java.awt.Window
import java.awt.event.*
import java.nio.file.Path
import javax.swing.*
import kotlin.system.exitProcess

/**
 * Main editor view controller that manages editing and execution of a script document.
 * Connects all functionality to the UI.
 *
 * Each editor window is managed by a unique instance.
 * @param path Path to document that should be opened upon display
 */
class Editor(path: Path? = null): EditorBase(), DocumentModelListener, MenuListener {
    /** UI frame with menu bar provided by [MenuFactory] and with app icon. */
    val frame = JFrame(ApplicationName).also {
        it.iconImages = listOf(ImageIcon(ApplicationIcon).image)
        it.jMenuBar = MenuFactory.create(this)
        it.contentPane = rootPanel
        it.pack()

        it.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        it.addWindowListener(object: WindowAdapter() {
            override fun windowClosing(e: WindowEvent) = fileExit()
        })
    }

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
        syntaxTextArea.syntaxScheme = SyntaxColors()
        autoCompletion.autoCompleteSingleChoices = false
        outputTextPane.editorKit = OutputEditorKit()
    }

    /** Document model, which maintains the buffer state. */
    val documentModel = DocumentModel(path, this)
    /** State machine, which manages the different UI states in regards to the script execution.  */
    val executionControl = ExecutionControl(this)

    init {
        newDocument(documentModel) // register document
        frame.isVisible = true
    }

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
    private fun save(file: Path?) = file?.let { documentModel.save(it) } != null

    /**
     * Prepares the editor for closing and cleans up its resources, if no unsaved changes will be lost.
     * If the document has unsaved changes, uses [trySave] to determine, whether the close operation should proceed.
     * @return `true` if there were no unsaved changes, or unsaved changes were saved by the user
     */
    private fun canClose(): Boolean {
        if (documentModel.isDirty) {
            when (dialog.shouldSaveFile(documentModel.documentName)) {
                true -> if (!trySave()) return false
                null -> return false
                false -> documentModel.close()
            }
        }
        executionControl.abort()
        return true
    }


    //region Menu Listener

    /** `true`, if other windows are visible on screen. */
    val areOtherWindowsVisible = Window.getWindows().any { it.isVisible && it != frame }

    override fun fileNew() { Editor() }
    override fun fileOpen() {
        dialog.openFile()?.let { fromPath ->
            if (!isMacOS && documentModel.path == null && !documentModel.isDirty)
                documentModel.open(fromPath)
            else
                Editor(fromPath)
        }
    }

    override fun fileClose() {
        if (isMacOS || areOtherWindowsVisible) fileExit()
        if (canClose()) documentModel.close()
    }

    override fun fileSave() { trySave() }
    override fun fileSaveAs() { trySaveAs() }

    override fun fileExit() {
        if (!canClose()) return
        frame.isVisible = false
        frame.dispose()
        if (!areOtherWindowsVisible) exitProcess(0)
    }

    //endregion

    //region Document Model Listener

    override fun newDocument(documentModel: DocumentModel) {
        syntaxTextArea.document = documentModel.document
        syntaxTextArea.forceReparsing(parserAdapter)
        documentModel.document.setSyntaxStyle(SyntaxTokenAdapter())

        autoCompletion.uninstall()
        autoCompletion.install(syntaxTextArea)
        autoCompletion.isAutoActivationEnabled = true

        infoChanged(documentModel)
        textChanged(documentModel)
    }

    override fun infoChanged(documentModel: DocumentModel) {
        var windowTitle = "$ApplicationName – ${documentModel.documentName}"
        if (documentModel.isDirty) windowTitle += "*"
        frame.title = windowTitle
        frame.rootPane.putClientProperty("Window.documentFile",     documentModel.path?.toFile())
        frame.rootPane.putClientProperty("Window.documentModified", documentModel.isDirty)
    }

    override fun textChanged(documentModel: DocumentModel) = executionControl.run()

    //endregion
}