package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.ApplicationName
import com.fkistner.SouthQuay.document.DocumentModel
import java.net.URL
import javax.swing.JFrame

class Editor(path: URL? = null): EditorBase(), DocumentModel.Listener, MenuListener {
    val frame = JFrame(ApplicationName).also { it.jMenuBar = Menu.create(this) }
    val dialog = Dialogs(frame)

    val documentModel = DocumentModel(path, this)
    val executionControl = ExecutionControl(this)

    init {
        evaluateButton.addActionListener { executionControl.run() }
        abortButton.addActionListener    { executionControl.abort() }

        frame.contentPane = panel
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }

    override fun newDocument(documentModel: DocumentModel) {
        syntaxTextArea.document = documentModel.document
        infoChanged(documentModel)
    }

    override fun infoChanged(documentModel: DocumentModel) {
        var windowTitle = "$ApplicationName – ${documentModel.documentName}"
        if (documentModel.isDirty) windowTitle += "*"
        frame.title = windowTitle
        frame.rootPane.putClientProperty("Window.documentModified", documentModel.isDirty)
    }

    override fun fileNew() {
        Editor()
    }

    override fun fileOpen() {
        dialog.openFile()?.let { fromURL ->
            if (documentModel.path == null && documentModel.isDirty == false)
                documentModel.open(fromURL)
            else
                Editor(fromURL)
        }
    }

    override fun fileClose() {
        if (documentModel.isDirty) {
            when (dialog.shouldSaveFile(documentModel.documentName)) {
                true -> {
                    fileSave()
                    return
                }
                null -> return
                else -> {}
            }
        }
        documentModel.close()
    }

    override fun fileSave() {
        documentModel.path?.let {
            documentModel.save(it)
            return
        }
        assert(documentModel.path == null)
        fileSaveAs()
    }

    override fun fileSaveAs() {
        dialog.saveFile()?.let {
            documentModel.save(it)
        }
    }
}