package com.fkistner.SouthQuay.UI

import java.awt.Dimension
import javax.swing.JScrollPane

/**
 * Scroll pane which does not adjust its preferred width according to its children for use in a split view.
 * @param width Preferred width
 */
class FixedWidthScrollPane: JScrollPane() {
    /** Fixed preferred size of the scroll pane. */
    override fun getPreferredSize(): Dimension = super.getPreferredSize().also {
        it.width = 0
    }
}
