package com.fkistner.SouthQuay

import org.fife.ui.rsyntaxtextarea.*
import java.io.File
import java.net.URL
import javax.swing.event.*


class DocumentModel: DocumentListener {
    var isDirty = false
    var document: RSyntaxDocument = createNewDocument()
    var path: URL? = null

    private val editorKit = RSyntaxTextAreaEditorKit()

    private fun createNewDocument() = RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE)

    fun open(file: URL) {
        path = file
        document = createNewDocument()
        isDirty = false
        editorKit.read(file.openStream(), document, 0)
        document.addDocumentListener(this)
    }

    fun close() {
        path = null
        document = createNewDocument()
        isDirty = false
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