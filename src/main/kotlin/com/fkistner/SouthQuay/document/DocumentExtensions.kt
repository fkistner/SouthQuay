package com.fkistner.SouthQuay.document

import javax.swing.text.*

/** All the text of the current document of the document model. */
val DocumentModel.text
    get() = document.text

/** All the text of the document. */
val PlainDocument.text: String
    get() = getText(0, length)
