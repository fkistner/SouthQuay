package com.fkistner.SouthQuay.document

import com.fkistner.SouthQuay.FileType
import org.fife.ui.rsyntaxtextarea.*
import java.io.File
import java.net.URL
import java.nio.file.Paths
import javax.swing.event.*

/**
 * Model that manages the document state, such as the untitled numbering and whether the document buffer is dirty.
 * @param path URL to initial file to load
 * @param documentListener Listener for document changes
 */
class DocumentModel(var path: URL? = null, val documentListener: DocumentModelListener? = null) : DocumentListener {
    companion object {
        /** Provider of read convenience methods. */
        private val editorKit = RSyntaxTextAreaEditorKit()

        /** Number of document that were assigned an untitled name. */
        var untitledCounter = 0
    }

    /** The current document. */
    var document = createNewDocument()
    /** The name of the current document. */
    var documentName: String
    /** Current dirty state of document, i.e. it contains unsaved changes. */
    var isDirty = false

    init {
        documentName = path.let {
            when (it) {
                null -> getUntitledName()
                else -> {
                    read(it)
                    it.fileName
                }
            }
        }
        initNewDocument()
    }

    /**
     * Generates a new untitled name.
     * @return Unique name for untitled document
     */
    private fun getUntitledName() = "Untitled ${++untitledCounter}"

    /**
     * Factory method for document.
     * @return Empty document with disabled syntax highlighting
     */
    private fun createNewDocument() = RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE)

    /**
     * Reset state information after [open], [close], [save] operation.
     * @param file New file path
     */
    private fun resetInfo(file: URL?) {
        isDirty = false
        path = file
        documentName = file?.fileName ?: getUntitledName()
    }

    /**
     * Read file into document.
     * @param file File to read
     */
    private fun read(file: URL) = editorKit.read(file.openStream(), document, 0).also { isDirty = false }

    /**
     * Adapts path to include [FileType] suffix, if not already present.
     * @param file Proposed file path
     * @return Accepted file path
     */
    fun adaptPath(file: URL): URL = when {
        file.path.endsWith(FileType) -> file
        else -> Paths.get(file.path + FileType).toUri().toURL()
    }


    /**
     * Creates a new document, optionally reading it from file.
     * @param file File path to read
     */
    private fun newDocument(file: URL? = null) {
        document = createNewDocument()
        resetInfo(file)
        file?.let(this::read)
        initNewDocument()
        file?.let { documentListener?.textChanged(this) }
    }

    /** Installs listener and signals [DocumentModel.Listener] */
    private fun initNewDocument() {
        document.addDocumentListener(this)
        documentListener?.newDocument(this)
    }

    /** Records change signalled by the current [document]. */
    private fun recordChange(): Unit = isDirty.let { wasDirty ->
        isDirty = true
        if (!wasDirty) documentListener?.infoChanged(this)
        documentListener?.textChanged(this)
    }


    /**
     * Opens the provided file and sets it as the new document.
     * @param file File path to read
     */
    fun open(file: URL) = newDocument(file)

    /** Closes the current file and creates new empty document. */
    fun close() = newDocument()

    /**
     * Saves the current file under the provided file path.
     * @param file Save destination
     */
    fun save(file: URL) {
        File(file.toURI()).outputStream().bufferedWriter().use {
            it.write(text)
        }
        resetInfo(file)
        documentListener?.infoChanged(this)
    }


    //region Document Listener

    override fun changedUpdate(e: DocumentEvent?) = Unit
    override fun insertUpdate (e: DocumentEvent?) = recordChange()
    override fun removeUpdate (e: DocumentEvent?) = recordChange()

    //endregion
}
