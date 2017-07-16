package com.fkistner.SouthQuay.document


class CountingDocumentListener: DocumentModel.Listener {
    var newDoc = 0
    var infoChanged = 0

    override fun newDocument(documentModel: DocumentModel) {
        newDoc++
    }

    override fun infoChanged(documentModel: DocumentModel) {
        infoChanged++
    }
}