package com.fkistner.SouthQuay.document

import org.junit.*
import java.nio.file.*

class DocumentModelTests {
    private val sampleFileName = "Sample.sq"
    private val sampleResource: Path
        get() = getResourceAsPath("/$sampleFileName") ?: throw AssertionError("Bad test setup.")

    @Before
    fun setup() {
        DocumentModel.Companion.untitledCounter = 0
    }

    @Test
    fun initial() {
        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        Assert.assertEquals(0, listener.newDoc)
        Assert.assertEquals(0, listener.infoChanged)
        Assert.assertEquals(0, listener.textChanged)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(null, documentModel.path)
        Assert.assertEquals(0, documentModel.document.length)
    }

    @Test
    fun openFile() {
        val path = sampleResource
        Assert.assertNotNull("Bad test setup.", path)

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        documentModel.open(path)

        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(0, listener.infoChanged)
        Assert.assertEquals(0, listener.textChanged)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(path.toString(), documentModel.path?.toString())
        Assert.assertEquals(path.readText(), documentModel.document.getText(0, documentModel.document.length))
    }

    @Test
    fun openFileInsert() {
        val path = sampleResource
        Assert.assertNotNull("Bad test setup.", path)

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        Assert.assertEquals(0, listener.newDoc)
        Assert.assertEquals(0, listener.infoChanged)

        documentModel.open(path)
        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(0, listener.infoChanged)
        Assert.assertEquals(false, documentModel.isDirty)

        documentModel.document.insertString(0, "Test", null)
        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(1, listener.infoChanged)
        Assert.assertEquals(1, listener.textChanged)
        Assert.assertEquals(true, documentModel.isDirty)

        documentModel.document.insertString(0, "Hello", null)
        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(1, listener.infoChanged)
        Assert.assertEquals(2, listener.textChanged)
        Assert.assertEquals(true, documentModel.isDirty)

        Assert.assertEquals(path.toString(), documentModel.path?.toString())
        Assert.assertEquals(145, documentModel.document.length)
    }

    @Test
    fun openFileRemove() {
        val path = sampleResource
        Assert.assertNotNull("Bad test setup.", path)

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        Assert.assertEquals(0, listener.newDoc)
        Assert.assertEquals(0, listener.infoChanged)
        Assert.assertEquals(0, listener.textChanged)
        Assert.assertEquals(false, documentModel.isDirty)

        documentModel.open(path)
        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(0, listener.infoChanged)
        Assert.assertEquals(0, listener.textChanged)
        Assert.assertEquals(false, documentModel.isDirty)

        documentModel.document.remove(5, 10)
        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(1, listener.infoChanged)
        Assert.assertEquals(1, listener.textChanged)
        Assert.assertEquals(true, documentModel.isDirty)

        documentModel.document.remove(22, 4)
        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(1, listener.infoChanged)
        Assert.assertEquals(2, listener.textChanged)
        Assert.assertEquals(true, documentModel.isDirty)

        Assert.assertEquals(path.toString(), documentModel.path?.toString())
        Assert.assertEquals(122, documentModel.document.length)
    }

    @Test
    fun openFileEditOpen() {
        val path = sampleResource
        Assert.assertNotNull("Bad test setup.", path)

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        documentModel.open(path)
        documentModel.document.insertString(0, "Test", null)
        documentModel.open(path)

        Assert.assertEquals(2, listener.newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(path.toString(), documentModel.path?.toString())
        Assert.assertEquals(136, documentModel.document.length)
    }

    @Test
    fun openFileEditClose() {
        val path = sampleResource
        Assert.assertNotNull("Bad test setup.", path)

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        documentModel.open(path)
        documentModel.document.insertString(0, "Test", null)
        documentModel.close()

        Assert.assertEquals(2, listener.newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(null, documentModel.path)
        Assert.assertEquals(0, documentModel.document.length)
    }

    @Test
    fun openFileEditSaveAs() {
        val path = sampleResource
        Assert.assertNotNull("Bad test setup.", path)

        val saveAsFile = Files.createTempFile("SQTest", ".sq")

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        documentModel.open(path)
        val insertedString = "Test"
        documentModel.document.insertString(0, insertedString, null)
        documentModel.save(saveAsFile)

        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(saveAsFile.toString(), documentModel.path?.toString())
        Assert.assertEquals(140, documentModel.document.length)

        val savedStream = Files.newInputStream(saveAsFile)
        val insertedBytes = insertedString.toByteArray()
        val prefixBytes = ByteArray(insertedBytes.count())
        Assert.assertEquals(insertedBytes.count(), savedStream.read(prefixBytes))
        Assert.assertArrayEquals(insertedBytes, prefixBytes)
        Assert.assertArrayEquals(sampleResource.readBytes(), savedStream.readBytes())
    }

    @Test
    fun openFileEditSave() {
        val tempFile = Files.createTempFile("SQTest", ".sq")
        Files.copy(sampleResource, tempFile, StandardCopyOption.REPLACE_EXISTING)

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        documentModel.open(tempFile)

        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(tempFile.toString(), documentModel.path?.toString())
        Assert.assertEquals(sampleResource.readText(), documentModel.document.getText(0, documentModel.document.length))

        val insertedString = "Test"
        documentModel.document.insertString(0, insertedString, null)
        documentModel.save(tempFile)

        Assert.assertEquals(1, listener.newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(tempFile.toString(), documentModel.path?.toString())
        Assert.assertEquals(140, documentModel.document.length)

        val savedStream = Files.newInputStream(tempFile)
        val insertedBytes = insertedString.toByteArray()
        val prefixBytes = ByteArray(insertedBytes.count())
        Assert.assertEquals(insertedBytes.count(), savedStream.read(prefixBytes))
        Assert.assertArrayEquals(insertedBytes, prefixBytes)
        Assert.assertArrayEquals(sampleResource.readBytes(), savedStream.readBytes())
    }

    @Test
    fun openAt() {
        val path = sampleResource
        Assert.assertNotNull("Bad test setup.", path)

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(path, listener)

        Assert.assertEquals(0, listener.newDoc)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(path.toString(), documentModel.path?.toString())
        Assert.assertEquals(path.readText(), documentModel.document.getText(0, documentModel.document.length))
    }

    @Test
    fun fileName() {
        val path = sampleResource
        Assert.assertNotNull("Bad test setup.", path)

        val documentModel = DocumentModel(path)
        Assert.assertEquals(sampleFileName, documentModel.documentName)
    }

    @Test
    fun untitledFileName() {
        val documentModel = DocumentModel()
        Assert.assertEquals("Untitled 1", documentModel.documentName)
    }

    @Test
    fun untitledMultiFileName() {
        val documentModel = DocumentModel()
        documentModel.close()
        Assert.assertEquals("Untitled 2", documentModel.documentName)
    }

    @Test
    fun saveFileSuffix() {
        val saveAsFile = Files.createTempFile("SQTest", "")

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        documentModel.document.insertString(0, "Hello", null)
        val file = documentModel.adaptPath(saveAsFile)
        documentModel.save(file)

        Assert.assertEquals(saveAsFile.toString()+".sq", documentModel.path?.toString())
    }

    @Test
    fun textChanged() {
        val saveAsFile = Files.createTempFile("SQTest", ".sq")

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(0, listener.infoChanged)
        Assert.assertEquals(0, listener.textChanged)

        documentModel.document.insertString(0, "Hello", null)
        Assert.assertEquals(true, documentModel.isDirty)
        Assert.assertEquals(1, listener.infoChanged)
        Assert.assertEquals(1, listener.textChanged)

        documentModel.document.insertString(5, " World", null)
        Assert.assertEquals(true, documentModel.isDirty)
        Assert.assertEquals(1, listener.infoChanged)
        Assert.assertEquals(2, listener.textChanged)

        documentModel.save(saveAsFile)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(2, listener.infoChanged)
        Assert.assertEquals(2, listener.textChanged)
    }

    @Test
    fun textChangedAfterOpen() {
        val saveAsFile = Files.createTempFile("SQTest", ".sq")

        val listener = CountingDocumentListener()
        val documentModel = DocumentModel(documentListener = listener)
        documentModel.open(sampleResource)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(0, listener.infoChanged)
        Assert.assertEquals(0, listener.textChanged)

        documentModel.document.insertString(0, "Hello", null)
        Assert.assertEquals(true, documentModel.isDirty)
        Assert.assertEquals(1, listener.infoChanged)
        Assert.assertEquals(1, listener.textChanged)

        documentModel.document.insertString(5, " World", null)
        Assert.assertEquals(true, documentModel.isDirty)
        Assert.assertEquals(1, listener.infoChanged)
        Assert.assertEquals(2, listener.textChanged)

        documentModel.save(saveAsFile)
        Assert.assertEquals(false, documentModel.isDirty)
        Assert.assertEquals(2, listener.infoChanged)
        Assert.assertEquals(2, listener.textChanged)
    }
}