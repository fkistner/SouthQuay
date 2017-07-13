package com.fkistner.SouthQuay

import org.fife.ui.rsyntaxtextarea.*
import java.io.File
import java.net.URL
import javax.swing.event.*


class DocumentModel(val newDocumentListener: ((DocumentModel) -> Unit)? = null) : DocumentListener {
    var isDirty = false
    var document= createNewDocument()
    var path: URL? = null

    private val editorKit = RSyntaxTextAreaEditorKit()
    private fun createNewDocument() = RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE)

    init {
        newDocumentListener?.invoke(this)
    }

    private fun newDocument(initAction: () -> Unit) {
        document = createNewDocument()
        initAction()
        newDocumentListener?.invoke(this)
    }

    fun open(file: URL) {
        newDocument {
            editorKit.read(file.openStream(), document, 0)
            path = file
            isDirty = false
            document.addDocumentListener(this)
        }
    }

    fun close() {
        newDocument {
            path = null
            isDirty = false
        }
    }

    fun save(file: URL) {
        val text = document.getText(0, document.length)
        File(file.toURI()).outputStream().bufferedWriter().use {
            it.write(text)
        }
        path = file
        isDirty = false
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