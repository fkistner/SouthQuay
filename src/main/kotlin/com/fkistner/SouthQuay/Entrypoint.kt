package com.fkistner.SouthQuay

import com.fkistner.SouthQuay.UI.Editor
import javax.swing.UIManager

fun main(args: Array<String>) {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.name", ApplicationName)
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    Editor()
}
