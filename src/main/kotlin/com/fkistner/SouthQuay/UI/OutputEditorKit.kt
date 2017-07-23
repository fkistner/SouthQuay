package com.fkistner.SouthQuay.UI

import javax.swing.text.*

/** Provides customization of the [Editor.outputTextPane]. */
class OutputEditorKit : StyledEditorKit() {
    override fun clone() = this
    override fun getViewFactory() = _viewFactory

    /** Paragraph view that layouts text only in regards to the view's height. */
    class NonBreakingParagraphView(elem: Element): ParagraphView(elem) {
        override fun layout(width: Int, height: Int)  = super.layout(Int.MAX_VALUE, height)
        override fun getMinimumSpan(axis: Int): Float = super.getPreferredSpan(axis)
    }

    /**
     * View factory the provides the [NonBreakingParagraphView] as paragraph element
     * and delegates all other instantiations to the default factory.
     */
    private val _viewFactory: ViewFactory

    init {
        val parent = super.getViewFactory()
        _viewFactory = ViewFactory { elem ->
            when (elem.name) {
                AbstractDocument.ParagraphElementName -> NonBreakingParagraphView(elem)
                else -> parent.create(elem)
            }
        }
    }
}
