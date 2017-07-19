package com.fkistner.SouthQuay.document

import java.net.URL
import java.nio.file.Paths
import javax.swing.text.*

/** Last path component including extension name. */
val URL.fileName
    get() = Paths.get(path).fileName.toString()

/** All the text of the current document of the document model. */
val DocumentModel.text
    get() = document.text

/** All the text of the document. */
val PlainDocument.text: String
    get() = getText(0, length)
