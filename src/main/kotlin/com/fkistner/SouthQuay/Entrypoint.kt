package com.fkistner.SouthQuay

import com.fkistner.SouthQuay.UI.Editor
import javax.swing.*

fun main(args: Array<String>) {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.name", ApplicationName)
    System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS")
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder())

    Editor()
}
