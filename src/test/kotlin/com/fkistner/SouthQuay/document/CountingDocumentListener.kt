package com.fkistner.SouthQuay.document


class CountingDocumentListener: DocumentModelListener {
    var newDoc = 0
    var infoChanged = 0
    var textChanged = 0

    override fun newDocument(documentModel: DocumentModel) {
        newDoc++
    }

    override fun infoChanged(documentModel: DocumentModel) {
        infoChanged++
    }

    override fun textChanged(documentModel: DocumentModel) {
        textChanged++
    }
}