package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.grammar.SQLangLexer
import com.fkistner.SouthQuay.grammar.SQLangLexer.*
import org.fife.ui.rsyntaxtextarea.*
import java.awt.*


object SyntaxColors: SyntaxScheme(true) {
    val defaultStyle = Style()

    val SecondaryColor  = Color.GRAY

    val KeywordColor    = Color(117,170,86) // green
    val OperatorColor   = Color(190,114,57) // orange
    val LiteralColor    = Color(59,167,229) // blue
    val IdentifierColor = Color(130,91,179) // purple
    val ErrorColor      = Color(185,76,117) // red

    val KeywordStyle    = Style(KeywordColor)
    val OperatorStyle   = Style(OperatorColor)
    val LiteralStyle    = Style(LiteralColor)
    val IdentifierStyle = Style(IdentifierColor)
    val WhitespaceStyle = Style(SecondaryColor)
    val ErrorStyle      = Style(ErrorColor)

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

    override fun getStyle(index: Int): Style {
        return super.getStyle(index) ?: defaultStyle
    }

    override fun restoreDefaults(baseFont: Font?) {
        super.restoreDefaults(baseFont)
    }
}
