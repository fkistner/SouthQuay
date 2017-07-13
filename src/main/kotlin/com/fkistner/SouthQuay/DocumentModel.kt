package com.fkistner.SouthQuay

import org.fife.ui.rsyntaxtextarea.*
import java.io.File
import java.net.URL
import java.nio.file.Paths
import javax.swing.event.*


class DocumentModel(path: URL? = null, val newDocumentListener: ((DocumentModel) -> Unit)? = null) : DocumentListener {
    var isDirty = false
    lateinit var document: RSyntaxDocument
    var fileName: String? = null

    var path: URL? = null
        set(value) {
            field = value
            isDirty = false
            if (value != null)
                fileName = Paths.get(value.path).fileName.toString()
        }

    companion object {
        private val editorKit = RSyntaxTextAreaEditorKit()
    }

    private fun createNewDocument() = RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE)

    init {
        when (path) {
            null -> close()
            else -> open(path)
        }
    }

    private fun newDocument(initAction: () -> Unit) {
        document = createNewDocument()
        initAction()
        newDocumentListener?.invoke(this)
    }

    fun open(file: URL) {
        newDocument {
            Companion.editorKit.read(file.openStream(), document, 0)
            path = file
            document.addDocumentListener(this)
        }
    }

    fun close() {
        newDocument {
            path = null
        }
    }

    fun save(file: URL) {
        val text = document.getText(0, document.length)
        File(file.toURI()).outputStream().bufferedWriter().use {
            it.write(text)
        }
        path = file
    }

    override fun changedUpdate(e: DocumentEvent?) {
    }

    override fun insertUpdate(e: DocumentEvent?) {
        isDirty = true
    }

    override fun removeUpdate(e: DocumentEvent?) {
        isDirty = true
    }
}