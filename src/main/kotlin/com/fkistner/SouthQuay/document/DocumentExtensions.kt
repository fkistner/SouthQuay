package com.fkistner.SouthQuay.document

import javax.swing.text.*


val DocumentModel.text
    get() = document.text

val PlainDocument.text: String
    get() = getText(0, length)
