package com.fkistner.SouthQuay.document

/** Interface for listeners of document events. */
interface DocumentModelListener {
    /**
     * Signals that a new document has become active.
     * @param documentModel Document model responsible
     */
    fun newDocument(documentModel: DocumentModel) = Unit

    /**
     * Signals that the document information have changed, i.e. dirty state, document name.
     * @param documentModel Document model responsible
     */

    fun infoChanged(documentModel: DocumentModel) = Unit
    /**
     * Signals that the text of the document has changed.
     * @param documentModel Document model responsible
     */
    fun textChanged(documentModel: DocumentModel) = Unit
}