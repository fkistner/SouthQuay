package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.UI.SyntaxTokenAdapter
import com.fkistner.SouthQuay.grammar.SQLangLexer.*
import org.fife.ui.rsyntaxtextarea.Token
import org.junit.*
import javax.swing.text.Segment


class SyntaxTokenTests {

    private fun assertTokenType(token: Int, type: Int) {
        Assert.assertEquals("\nExpected :${VOCABULARY.getSymbolicName(token)}\n" +
                "Actual   :${VOCABULARY.getSymbolicName(type)}", token, type)
    }

    @Test
    fun tokenTranslation() {
        val charArray = "var i = map({1, 4}, n -> 2 * n)".toCharArray()
        val text = Segment(charArray, 0, charArray.size)

        var tokenList: Token? = SyntaxTokenAdapter.getTokenList(text, 0, 33)
        Assert.assertEquals(charArray, tokenList!!.textArray)
        Assert.assertEquals(0, tokenList.textOffset)
        Assert.assertEquals(3, tokenList.length())
        Assert.assertEquals(33, tokenList.offset)
        assertTokenType(VAR, tokenList.type)

        for (token in arrayOf(Identifier, EQ, Identifier, PAREN_LEFT, SEQ_LEFT, Integer, COMMA, Integer, SEQ_RIGHT,
                COMMA, Identifier, LAM, Integer, MUL, Identifier, PAREN_RIGHT)) {
            tokenList = tokenList!!.nextToken
            while (tokenList!!.type == Whitespace) tokenList = tokenList.nextToken
            assertTokenType(token, tokenList.type)
        }
        Assert.assertEquals(charArray, tokenList!!.textArray)
        Assert.assertEquals(30, tokenList.textOffset)
        Assert.assertEquals(1, tokenList.length())
        Assert.assertEquals(63, tokenList.offset)
        assertTokenType(PAREN_RIGHT, tokenList.type)
    }

    @Test
    fun tokenTranslationSliceCharArray() {
        val srcArray = "var i = map({1, 4}, n -> 2 * n)".toCharArray()
        val charArray = CharArray(200)
        System.arraycopy(srcArray, 0, charArray, 57, srcArray.size)
        val text = Segment(charArray, 57, srcArray.size)

        var tokenList: Token? = SyntaxTokenAdapter.getTokenList(text, 0, 11)
        Assert.assertEquals(charArray, tokenList!!.textArray)
        Assert.assertEquals(57, tokenList.textOffset)
        Assert.assertEquals(3, tokenList.length())
        Assert.assertEquals(11, tokenList.offset)
        assertTokenType(VAR, tokenList.type)

        for (token in arrayOf(Identifier, EQ, Identifier, PAREN_LEFT, SEQ_LEFT, Integer, COMMA, Integer, SEQ_RIGHT,
                COMMA, Identifier, LAM, Integer, MUL, Identifier, PAREN_RIGHT)) {
            tokenList = tokenList!!.nextToken
            while (tokenList!!.type == Whitespace) tokenList = tokenList.nextToken
            assertTokenType(token, tokenList.type)
        }
        Assert.assertEquals(charArray, tokenList!!.textArray)
        Assert.assertEquals(87, tokenList.textOffset)
        Assert.assertEquals(1, tokenList.length())
        Assert.assertEquals(41, tokenList.offset)
        assertTokenType(PAREN_RIGHT, tokenList.type)
    }
}