package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.grammar.SQLangLexer
import com.fkistner.SouthQuay.grammar.SQLangLexer.*
import org.antlr.v4.runtime.CharStreams
import org.fife.ui.rsyntaxtextarea.*
import java.io.CharArrayReader
import javax.swing.text.Segment

object SyntaxTokenMaker: TokenMakerBase() {
    override fun getTokenList(text: Segment, initialTokenType: Int, startOffset: Int): Token {
        resetTokenList()

        val charStream = CharStreams.fromReader(CharArrayReader(text.array, text.offset, text.count))
        val lexer = SQLangLexer(charStream)

        var lexerToken = lexer.nextToken()
        while (lexerToken.type != EOF) {
            val textStartIndex = text.offset + lexerToken.startIndex
            val textStopIndex  = text.offset + lexerToken.stopIndex
            addToken(text, textStartIndex, textStopIndex, lexerToken.type, startOffset + lexerToken.startIndex)
            lexerToken = lexer.nextToken()
        }

        addNullToken()
        return firstToken
    }
}