package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.*
import java.awt.FileDialog
import java.net.URL
import java.nio.file.Paths
import javax.swing.*

/**
 * Controller for displaying file open/save dialogs and asking about unsaved changes in files.
 *
 * @param frame Frame the dialogs are to be attached to
 */
class Dialogs(val frame: JFrame) {
    /**
     * Presents an open file dialog for script file.
     * @return The URL of the selected file or `null`
     */
    fun openFile() = present(FileDialog(frame, "Open $ApplicationName Script", FileDialog.LOAD))

    /**
     * Presents a save file dialog for script file.
     * @return The URL of the selected file or `null`
     */
    fun saveFile() = present(FileDialog(frame, "Save $ApplicationName Script", FileDialog.SAVE))

    /**
     * Presents the given file dialog, filters for script files, and translates the selected file to URL.
     * @return The URL of the selected file or `null`
     */
    private fun present(fileDialog: FileDialog): URL? {
        fileDialog.file = "*" + FileType
        fileDialog.setFilenameFilter { _, name -> name.endsWith(".sq") }
        fileDialog.setLocationRelativeTo(frame)
        fileDialog.isVisible = true

        val file = fileDialog.file?.trim()
        val dir = fileDialog.directory?.trim()
        if (file == null || dir == null) return null

        return Paths.get(dir, file).toUri().toURL()
    }

    /**
     * Presents a dialog box to the user to confirm, whether to save unsaved changes.
     * @param file Name of the file containing unsaved changes
     * @return `true` if changes should be saved, `false` if changes should be discarded, and `null` if the user cancelled
     */
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
