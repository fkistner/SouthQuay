package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.DocumentModel
import org.junit.*

class DocumentModelTests {
    @Test
    fun initial() {
        val documentModel = DocumentModel()
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(null, documentModel.path)
        Assert.assertEquals(0, documentModel.document.length)
    }

    @Test
    fun openFile() {
        val url = javaClass.getResource("/Sample.sq")
        Assert.assertNotNull("Bad test setup.", url)

        val documentModel = DocumentModel()
        documentModel.open(url)

        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(134, documentModel.document.length)
    }
}