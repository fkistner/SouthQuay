package com.fkistner.SouthQuay.UI

import javax.swing.text.*


object OutputEditorKit : StyledEditorKit() {
    override fun clone() = this
    override fun getViewFactory() = Factory

    class NonBreakingParagraphView(elem: Element): ParagraphView(elem) {
        override fun layout(width: Int, height: Int) = super.layout(Int.MAX_VALUE, height)
        override fun getMinimumSpan(axis: Int): Float = super.getPreferredSpan(axis)
    }

    object Factory: ViewFactory {
        val parent: ViewFactory = StyledEditorKit().viewFactory

        override fun create(elem: Element): View = when (elem.name) {
            AbstractDocument.ParagraphElementName -> NonBreakingParagraphView(elem)
            else -> parent.create(elem)
        }
    }
}
