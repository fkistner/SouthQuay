package com.fkistner.SouthQuay

import org.fife.ui.rsyntaxtextarea.*
import java.net.URL

class DocumentModel {
    var isDirty = false
    var document: RSyntaxDocument = createNewDocument()
    var path: URL? = null

    private val editorKit = RSyntaxTextAreaEditorKit()

    private fun createNewDocument() = RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE)

    fun open(file: URL) {
        path = file
        document = createNewDocument()
        editorKit.read(file.openStream(), document, 0)
    }
}