package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.ApplicationName
import com.fkistner.SouthQuay.document.DocumentModel
import org.fife.ui.autocomplete.AutoCompletion
import java.awt.Window
import java.awt.event.*
import java.net.URL
import javax.swing.JFrame

class Editor(path: URL? = null): EditorBase(), DocumentModel.Listener, MenuListener {
    val frame = JFrame(ApplicationName).also { it.jMenuBar = Menu.create(this) }
    val dialog = Dialogs(frame)

    val autoCompletion = AutoCompletion(CompletionProposalGenerator).also {
        it.install(syntaxTextArea)
    }

    init {
        evaluateButton.addActionListener { executionControl.run() }
        abortButton.addActionListener    { executionControl.abort() }

        syntaxTextArea.addParser(ParserAdapter)
        outputTextArea.editorKit = OutputEditorKit

        frame.addWindowListener(object: WindowAdapter() {
            override fun windowClosing(e: WindowEvent) = fileClose()
        })
        frame.contentPane = panel
        frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }

    val documentModel = DocumentModel(path, this)
    val executionControl = ExecutionControl(this)

    override fun newDocument(documentModel: DocumentModel) {
        syntaxTextArea.document = documentModel.document
        documentModel.document.setSyntaxStyle(SyntaxTokenMaker)
        syntaxTextArea.syntaxScheme = SyntaxColors
        infoChanged(documentModel)
    }

    override fun infoChanged(documentModel: DocumentModel) {
        var windowTitle = "$ApplicationName – ${documentModel.documentName}"
        if (documentModel.isDirty) windowTitle += "*"
        frame.title = windowTitle
        frame.rootPane.putClientProperty("Window.documentModified", documentModel.isDirty)
    }

    override fun textChanged(documentModel: DocumentModel) = executionControl.run()

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
        frame.isVisible = false
        frame.dispose()
        if (Window.getWindows().none { it.isVisible }) System.exit(0)
    }

    override fun fileSave() { trySave() }
    override fun fileSaveAs() { trySaveAs() }

    private fun trySave() = save(documentModel.path) || trySaveAs()
    private fun trySaveAs() = save(dialog.saveFile()?.let(documentModel::adaptPath))
    private fun save(file: URL?) = file?.let { documentModel.save(it) } != null
}