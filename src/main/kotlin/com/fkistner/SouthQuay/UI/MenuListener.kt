package com.fkistner.SouthQuay.UI

/** Listener for file menu actions. */
interface MenuListener {
    /** Opens a new editor. */
    fun fileNew()    {}
    /** Opens an existing file in a new editor. */
    fun fileOpen()   {}
    /** Closes an editor. */
    fun fileClose()  {}
    /** Saves the file open in an editor. */
    fun fileSave()   {}
    /** Saves the file open in an editor in a new location. */
    fun fileSaveAs() {}
}