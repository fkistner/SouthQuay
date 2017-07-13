package com.fkistner.SouthQuay

import com.fkistner.SouthQuay.UI.*
import java.awt.MenuBar
import javax.swing.JFrame

fun main(args: Array<String>) {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.name", "South Quay");

    val frame = JFrame("South Quay")
    val editor = Editor()
    frame.jMenuBar = Menu.create()
    frame.contentPane = editor.panel
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true
}
