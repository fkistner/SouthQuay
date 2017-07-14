package com.fkistner.SouthQuay

import com.fkistner.SouthQuay.UI.*
import com.fkistner.SouthQuay.document.DocumentModel
import java.awt.FileDialog
import java.net.URL
import java.nio.file.Paths
import javax.swing.*

fun main(args: Array<String>) {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.name", "South Quay")

    newEditor()
}

const private val FileType = "*.sq"

private fun newEditor(path: URL? = null) {
    val frame = JFrame("South Quay")
    val editor = Editor()

    val documentModel = DocumentModel(path) { documentModel ->
        editor.syntaxTextArea.document = documentModel.document
        frame.title = "South Quay – ${documentModel.fileName}"
    }

    frame.jMenuBar = Menu.create(object : MenuListener {
        override fun fileNew() {
            newEditor()
        }

        override fun fileOpen() {
            openFileDialog()?.let { fromURL ->
                if (documentModel.path == null && documentModel.isDirty == false)
                    documentModel.open(fromURL)
                else
                    newEditor(fromURL)
            }
        }

        override fun fileClose() {
            if (documentModel.isDirty) {
                when (shouldSaveDialog()) {
                    true -> {
                        fileSave()
                        return
                    }
                    null -> return
                    else -> {}
                }
            }
            documentModel.close()
        }

        override fun fileSave() {
            documentModel.path?.let {
                documentModel.save(it)
                return
            }
            assert(documentModel.path == null)
            fileSaveAs()
        }

        override fun fileSaveAs() {
            saveFileDialog()?.let {
                documentModel.save(it)
            }
        }

        private fun openFileDialog(): URL? {
            val fileDialog = FileDialog(frame, "Open South Quay Script", FileDialog.LOAD)
            return present(fileDialog)
        }

        private fun saveFileDialog(): URL? {
            val fileDialog = FileDialog(frame, "Save South Quay Script", FileDialog.SAVE)
            return present(fileDialog)
        }

        private fun present(fileDialog: FileDialog): URL? {
            fileDialog.file = FileType
            fileDialog.setFilenameFilter { _, name -> name.endsWith(".sq") }
            fileDialog.setLocationRelativeTo(frame)
            fileDialog.isVisible = true

            val file = fileDialog.file?.trim()
            val dir = fileDialog.directory?.trim()
            if (file == null || dir == null) return null

            return Paths.get(dir, file).toUri().toURL()
        }

        private fun shouldSaveDialog(): Boolean? {
            val options = arrayOf("Save", "Discard", "Cancel")
            val n = JOptionPane.showOptionDialog(frame, "There are unsaved changes in ${documentModel.fileName}.\n" +
                    "Do you want to save them?",
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    options, options[0])
            return when (n) {
                0 -> true
                1 -> false
                else -> null
            }
        }
    })

    frame.contentPane = editor.panel
    frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
    frame.pack()
    frame.isVisible = true
}
