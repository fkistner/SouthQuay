package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.DocumentModel
import org.junit.*
import java.io.*
import java.nio.file.Files

class DocumentModelTests {
    @Test
    fun initial() {
        val documentModel = DocumentModel()
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(null, documentModel.path)
        Assert.assertEquals(0, documentModel.document.length)
    }

    private val sampleResource = "/Sample.sq"

    @Test
    fun openFile() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        val documentModel = DocumentModel()
        documentModel.open(url)

        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(url.readText(), documentModel.document.getText(0, documentModel.document.length))
    }

    @Test
    fun openFileInsert() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        val documentModel = DocumentModel()
        documentModel.open(url)
        documentModel.document.insertString(0, "Test", null)

        Assert.assertEquals(true, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(138, documentModel.document.length)
    }

    @Test
    fun openFileRemove() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        val documentModel = DocumentModel()
        documentModel.open(url)
        documentModel.document.remove(5, 10)

        Assert.assertEquals(true, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(124, documentModel.document.length)
    }

    @Test
    fun openFileEditOpen() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        val documentModel = DocumentModel()
        documentModel.open(url)
        documentModel.document.insertString(0, "Test", null)
        documentModel.open(url)

        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(134, documentModel.document.length)
    }

    @Test
    fun openFileEditClose() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        val documentModel = DocumentModel()
        documentModel.open(url)
        documentModel.document.insertString(0, "Test", null)
        documentModel.close()

        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(null, documentModel.path)
        Assert.assertEquals(0, documentModel.document.length)
    }

    @Test
    fun openFileEditSaveAs() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        val saveAsFile = File.createTempFile("SQTest", ".sq").toURI().toURL()

        val documentModel = DocumentModel()
        documentModel.open(url)
        val insertedString = "Test"
        documentModel.document.insertString(0, insertedString, null)
        documentModel.save(saveAsFile)

        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(saveAsFile.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(138, documentModel.document.length)

        val savedStream = saveAsFile.openStream()

        val insertedBytes = insertedString.toByteArray()
        val prefixBytes = ByteArray(insertedBytes.count())
        Assert.assertEquals(insertedBytes.count(), savedStream.read(prefixBytes))
        Assert.assertArrayEquals(insertedBytes, prefixBytes)
        Assert.assertArrayEquals(javaClass.getResourceAsStream(sampleResource).readBytes(), savedStream.readBytes())
    }
}