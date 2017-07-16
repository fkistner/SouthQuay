package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.*
import java.awt.FileDialog
import java.net.URL
import java.nio.file.Paths
import javax.swing.*


class Dialogs(val frame: JFrame) {
    fun openFile() = present(FileDialog(frame, "Open $ApplicationName Script", FileDialog.LOAD))
    fun saveFile() = present(FileDialog(frame, "Save $ApplicationName Script", FileDialog.SAVE))

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

    fun shouldSaveFile(file: String): Boolean? {
        val options = arrayOf("Save", "Delete", "Cancel")
        val n = JOptionPane.showOptionDialog(frame, "There are unsaved changes in $file.\n" +
                "Do you want to save them?",
                "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
                options, options[0])
        return when (n) {
            0 -> true
            1 -> false
            else -> null
        }
    }
}
