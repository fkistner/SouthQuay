package com.fkistner.SouthQuay

import com.fkistner.SouthQuay.UI.Editor
import javax.swing.JFrame

fun main(args: Array<String>) {
    val frame = JFrame("Editor")
    val editor = Editor()
    frame.contentPane = editor.panel
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true
}
