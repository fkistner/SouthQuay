package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.DocumentModel
import org.junit.*
import java.io.File
import java.nio.file.*

class DocumentModelTests {
    private val sampleFileName = "Sample.sq"
    private val sampleResource = "/$sampleFileName"

    @Test
    fun initial() {
        var newDoc = 0
        val documentModel = DocumentModel { newDoc++ }
        Assert.assertEquals(1, newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(null, documentModel.path)
        Assert.assertEquals(0, documentModel.document.length)
    }

    @Test
    fun openFile() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        var newDoc = 0
        val documentModel = DocumentModel { newDoc++ }
        documentModel.open(url)

        Assert.assertEquals(2, newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(url.readText(), documentModel.document.getText(0, documentModel.document.length))
    }

    @Test
    fun openFileInsert() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        var newDoc = 0
        val documentModel = DocumentModel { newDoc++ }
        documentModel.open(url)
        documentModel.document.insertString(0, "Test", null)

        Assert.assertEquals(2, newDoc)
        Assert.assertEquals(true, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(138, documentModel.document.length)
    }

    @Test
    fun openFileRemove() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        var newDoc = 0
        val documentModel = DocumentModel { newDoc++ }
        documentModel.open(url)
        documentModel.document.remove(5, 10)

        Assert.assertEquals(2, newDoc)
        Assert.assertEquals(true, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(124, documentModel.document.length)
    }

    @Test
    fun openFileEditOpen() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        var newDoc = 0
        val documentModel = DocumentModel { newDoc++ }
        documentModel.open(url)
        documentModel.document.insertString(0, "Test", null)
        documentModel.open(url)

        Assert.assertEquals(3, newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(134, documentModel.document.length)
    }

    @Test
    fun openFileEditClose() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        var newDoc = 0
        val documentModel = DocumentModel { newDoc++ }
        documentModel.open(url)
        documentModel.document.insertString(0, "Test", null)
        documentModel.close()

        Assert.assertEquals(3, newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(null, documentModel.path)
        Assert.assertEquals(0, documentModel.document.length)
    }

    @Test
    fun openFileEditSaveAs() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        val saveAsFile = File.createTempFile("SQTest", ".sq").toURI().toURL()

        var newDoc = 0
        val documentModel = DocumentModel { newDoc++ }
        documentModel.open(url)
        val insertedString = "Test"
        documentModel.document.insertString(0, insertedString, null)
        documentModel.save(saveAsFile)

        Assert.assertEquals(2, newDoc)
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

    @Test
    fun openFileEditSave() {
        val resourceStream = javaClass.getResourceAsStream(sampleResource)
        Assert.assertNotNull("Bad test setup.", resourceStream)

        val tempFile = File.createTempFile("SQTest", ".sq")
        val url = tempFile.toURI().toURL()

        Files.copy(resourceStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

        var newDoc = 0
        val documentModel = DocumentModel { newDoc++ }
        documentModel.open(url)

        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(javaClass.getResource(sampleResource).readText(), documentModel.document.getText(0, documentModel.document.length))

        val insertedString = "Test"
        documentModel.document.insertString(0, insertedString, null)
        documentModel.save(url)

        Assert.assertEquals(2, newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(138, documentModel.document.length)

        val savedStream = url.openStream()
        val insertedBytes = insertedString.toByteArray()
        val prefixBytes = ByteArray(insertedBytes.count())
        Assert.assertEquals(insertedBytes.count(), savedStream.read(prefixBytes))
        Assert.assertArrayEquals(insertedBytes, prefixBytes)
        Assert.assertArrayEquals(javaClass.getResourceAsStream(sampleResource).readBytes(), savedStream.readBytes())
    }

    @Test
    fun openAt() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        var newDoc = 0
        val documentModel = DocumentModel(url) { newDoc++ }

        Assert.assertEquals(1, newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(url.toExternalForm(), documentModel.path?.toExternalForm())
        Assert.assertEquals(url.readText(), documentModel.document.getText(0, documentModel.document.length))
    }

    @Test
    fun fileName() {
        val url = javaClass.getResource(sampleResource)
        Assert.assertNotNull("Bad test setup.", url)

        val documentModel = DocumentModel(url)
        Assert.assertEquals(sampleFileName, documentModel.fileName)
    }
}