package com.fkistner.SouthQuay.UI

import java.awt.*
import java.awt.event.*
import javax.swing.*

object Menu {
    fun create(): JMenuBar {
        val menuShortcutKeyMask = Toolkit.getDefaultToolkit().menuShortcutKeyMask
        val menuBar = JMenuBar()

        val fileMenu = menuBar.add(JMenu("File"))
        fileMenu.mnemonic = KeyEvent.VK_F

        val newItem = fileMenu.add(JMenuItem("New…", KeyEvent.VK_N))
        newItem.accelerator = KeyStroke.getKeyStroke('N', menuShortcutKeyMask)

        val openItem = fileMenu.add(JMenuItem("Open…", KeyEvent.VK_O))
        openItem.accelerator = KeyStroke.getKeyStroke('O', menuShortcutKeyMask)

        fileMenu.addSeparator()

        val closeItem = fileMenu.add(JMenuItem("Close", KeyEvent.VK_C))
        closeItem.accelerator = KeyStroke.getKeyStroke('W', menuShortcutKeyMask)

        val saveItem = fileMenu.add(JMenuItem("Save", KeyEvent.VK_S))
        saveItem.accelerator = KeyStroke.getKeyStroke('S', menuShortcutKeyMask)

        val saveAsItem = fileMenu.add(JMenuItem("Save As…", KeyEvent.VK_A))
        saveAsItem.accelerator = KeyStroke.getKeyStroke('S', menuShortcutKeyMask or Event.ALT_MASK)

        return menuBar
    }
}