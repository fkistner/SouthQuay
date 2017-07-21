package com.fkistner.SouthQuay

import com.fkistner.SouthQuay.UI.Editor
import javax.swing.*

/**
 * Entry point to South Quay, which is registered as *main class*.
 *
 * Sets platform specific look and feel settings and creates the initial [Editor] instance.
 *
 * @param args Command line arguments are ignored at this time
 */
fun main(args: Array<String>) {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.name", ApplicationName)
    System.setProperty("apple.awt.application.icon", ApplicationIcon.path)
    System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS")
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    Editor()
}
