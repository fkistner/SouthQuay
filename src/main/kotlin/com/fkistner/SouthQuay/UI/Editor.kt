package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.*
import com.fkistner.SouthQuay.document.DocumentModel
import com.fkistner.SouthQuay.interpreter.*
import com.fkistner.SouthQuay.parser.*
import java.awt.FileDialog
import java.io.*
import java.net.URL
import java.nio.file.Paths
import java.util.concurrent.Executors
import javax.swing.*

class Editor(path: URL? = null): EditorBase() {
    val frame = JFrame(ApplicationName)

    val documentModel = DocumentModel(path) { documentModel ->
        syntaxTextArea.document = documentModel.document
        frame.title = "$ApplicationName – ${documentModel.fileName}"
    }

    val executionControl = ExecutionControl(this)

    init {
        // frame.getRootPane().putClientProperty("Window.documentModified", Boolean.TRUE)

        frame.jMenuBar = Menu.create(object : MenuListener {
            override fun fileNew() {
                Editor()
            }

            override fun fileOpen() {
                openFileDialog()?.let { fromURL ->
                    if (documentModel.path == null && documentModel.isDirty == false)
                        documentModel.open(fromURL)
                    else
                        Editor(fromURL)
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
                val fileDialog = FileDialog(frame, "Open $ApplicationName Script", FileDialog.LOAD)
                return present(fileDialog)
            }

            private fun saveFileDialog(): URL? {
                val fileDialog = FileDialog(frame, "Save $ApplicationName Script", FileDialog.SAVE)
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

        evaluateButton.addActionListener {
            executionControl.run()
        }
        abortButton.addActionListener {
            executionControl.abort()
        }

        frame.contentPane = panel
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }
}