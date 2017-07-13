package com.fkistner.SouthQuay.UI

import java.awt.*
import java.awt.event.*
import javax.swing.*

object Menu {
    fun create(menuListener: MenuListener? = null): JMenuBar {
        val menuShortcutKeyMask = Toolkit.getDefaultToolkit().menuShortcutKeyMask
        val menuBar = JMenuBar()

        val fileMenu = menuBar.add(JMenu("File"))
        fileMenu.mnemonic = KeyEvent.VK_F

        val newItem = fileMenu.add(JMenuItem("New…", KeyEvent.VK_N))
        newItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_N, menuShortcutKeyMask)
        newItem.addActionListener { menuListener?.fileNew() }

        val openItem = fileMenu.add(JMenuItem("Open…", KeyEvent.VK_O))
        openItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, menuShortcutKeyMask)
        openItem.addActionListener { menuListener?.fileOpen() }

        fileMenu.addSeparator()

        val closeItem = fileMenu.add(JMenuItem("Close", KeyEvent.VK_C))
        closeItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_W, menuShortcutKeyMask)
        closeItem.addActionListener { menuListener?.fileClose() }

        val saveItem = fileMenu.add(JMenuItem("Save", KeyEvent.VK_S))
        saveItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, menuShortcutKeyMask)
        saveItem.addActionListener { menuListener?.fileSave() }

        val saveAsItem = fileMenu.add(JMenuItem("Save As…", KeyEvent.VK_A))
        saveAsItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, menuShortcutKeyMask or Event.ALT_MASK)
        saveAsItem.addActionListener { menuListener?.fileSaveAs() }

        return menuBar
    }
}