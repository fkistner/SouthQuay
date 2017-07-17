package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.grammar.SQLangLexer
import com.fkistner.SouthQuay.grammar.SQLangLexer.*
import org.fife.ui.rsyntaxtextarea.*
import java.awt.*


object SyntaxColors: SyntaxScheme(true) {
    val defaultStyle = Style()

    init {
        val styleArray = arrayOfNulls<Style>(VOCABULARY.maxTokenType + 1)

        val keyword = Style(Color(117,170,86)) // green
        for (t in arrayOf(PRINT, OUT, VAR))
            styleArray[t] = keyword

        val ops = Style(Color(190,114,57)) // orange
        for (t in arrayOf(PLUS, MINUS, MUL, DIV, POW, EQ, LAM))
            styleArray[t] = ops

        val literal = Style(Color(59,167,229)) // blue
        for (t in arrayOf(SQLangLexer.String, Integer, Real))
            styleArray[t] = literal

        val identifier = Style(Color(130,91,179)) // purple
        styleArray[Identifier] = identifier

        val whitespace = Style(Color.GRAY)
        styleArray[Whitespace] = whitespace

        val error = Style(Color(185,76,117)) // red
        styleArray[Error] = error

        styles = styleArray
    }

    override fun getStyle(index: Int): Style {
        return super.getStyle(index) ?: defaultStyle
    }

    override fun restoreDefaults(baseFont: Font?) {
        super.restoreDefaults(baseFont)
    }
}