package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.grammar.SQLangLexer
import com.fkistner.SouthQuay.grammar.SQLangLexer.*
import org.fife.ui.rsyntaxtextarea.*
import java.awt.*

/**
 * Syntax scheme for South Quay and color provider.
 *
 * The primary UI color is provided by the system as default.
 */
object SyntaxColors: SyntaxScheme(true) {
    /** Default text style. */
    val defaultStyle = Style()

    /** Secondary UI color. */
    val SecondaryColor: Color = Color.GRAY

    /** Syntax highlight color for keywords. */
    val KeywordColor    = Color(117,170,86) // green
    /** Syntax highlight color for operators. */
    val OperatorColor   = Color(190,114,57) // orange
    /** Syntax highlight color for literals. */
    val LiteralColor    = Color(59,167,229) // blue
    /** Syntax highlight color for identifier. */
    val IdentifierColor = Color(130,91,179) // purple
    /** Syntax highlight color for errors. */
    val ErrorColor      = Color(185,76,117) // red

    private val KeywordStyle    = Style(KeywordColor)
    private val OperatorStyle   = Style(OperatorColor)
    private val LiteralStyle    = Style(LiteralColor)
    private val IdentifierStyle = Style(IdentifierColor)
    private val WhitespaceStyle = Style(SecondaryColor)
    private val ErrorStyle      = Style(ErrorColor)

    init {
        val styleArray = arrayOfNulls<Style>(VOCABULARY.maxTokenType + 1)

        for (t in arrayOf(PRINT, OUT, VAR))
            styleArray[t] = KeywordStyle

        for (t in arrayOf(PLUS, MINUS, MUL, DIV, POW, EQ, LAM))
            styleArray[t] = OperatorStyle

        for (t in arrayOf(SQLangLexer.String, Integer, Real))
            styleArray[t] = LiteralStyle

        styleArray[Identifier] = IdentifierStyle
        styleArray[Whitespace] = WhitespaceStyle
        styleArray[Error]      = ErrorStyle

        styles = styleArray
    }

    override fun getStyle(index: Int) = super.getStyle(index) ?: defaultStyle
    override fun restoreDefaults(baseFont: Font?) = super.restoreDefaults(baseFont)
}
