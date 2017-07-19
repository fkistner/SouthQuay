package com.fkistner.SouthQuay.UI

import java.awt.Dimension
import javax.swing.JScrollPane

/**
 * Scroll pane which does adjust its preferred size according to its children.
 * @param width Preferred width
 * @param height Preferred height
 */
class NonResizingScrollPane(width: Int = 0, height: Int = 0): JScrollPane() {
    /** Fixed preferred size of the scroll pane. */
    val fixedPreferredSize = Dimension(width, height)
    override fun getPreferredSize(): Dimension = fixedPreferredSize
}
