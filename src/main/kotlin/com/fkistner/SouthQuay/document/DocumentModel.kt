package com.fkistner.SouthQuay.document

import org.fife.ui.rsyntaxtextarea.*
import java.io.File
import java.net.URL
import java.nio.file.Paths
import javax.swing.event.*


class DocumentModel(var path: URL? = null, val documentListener: Listener? = null) : DocumentListener {
    companion object {
        private val editorKit = RSyntaxTextAreaEditorKit()
        var untitledCounter = 0
    }

    var document = createNewDocument()
    var documentName: String
    var isDirty = false

    private fun getFileName(url: URL) = Paths.get(url.path).fileName.toString()
    private fun getUntitledName() = "Untitled ${++untitledCounter}"
    private fun createNewDocument() = RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE)

    fun adaptPath(file: URL): URL = when {
        file.path.endsWith(".sq") -> file
        else -> Paths.get(file.path + ".sq").toUri().toURL()
    }

    init {
        documentName = path.let {
            when (it) {
                null -> getUntitledName()
                else -> {
                    read(it)
                    getFileName(it)
                }
            }
        }
        initNewDocument()
    }

    private fun resetInfo(file: URL?) {
        isDirty = false
        path = file
        documentName = file?.let(this::getFileName) ?: getUntitledName()
    }

    private fun newDocument(file: URL? = null) {
        document = createNewDocument()
        resetInfo(file)
        file?.let(this::read)
        initNewDocument()
    }

    private fun initNewDocument() {
        document.addDocumentListener(this)
        documentListener?.newDocument(this)
    }

    private fun read(file: URL) = editorKit.read(file.openStream(), document, 0).also { isDirty = false }

    fun open(file: URL) = newDocument(file)
    fun close() = newDocument()

    fun save(file: URL) {
        File(file.toURI()).outputStream().bufferedWriter().use {
            it.write(text)
        }
        resetInfo(file)
        documentListener?.infoChanged(this)
    }

    override fun changedUpdate(e: DocumentEvent?) = Unit
    override fun insertUpdate(e: DocumentEvent?) = recordChange()
    override fun removeUpdate(e: DocumentEvent?) = recordChange()

    private fun recordChange(): Unit = isDirty.let { wasDirty ->
        isDirty = true
        if (!wasDirty) documentListener?.infoChanged(this)
        documentListener?.textChanged(this)
    }

    interface Listener {
        fun newDocument(documentModel: DocumentModel) = Unit
        fun infoChanged(documentModel: DocumentModel) = Unit
        fun textChanged(documentModel: DocumentModel) = Unit
    }
}
