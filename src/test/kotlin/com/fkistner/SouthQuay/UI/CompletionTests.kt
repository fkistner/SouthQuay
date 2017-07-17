package com.fkistner.SouthQuay.UI

import org.junit.*
import javax.swing.JTextArea


class CompletionTests {

    companion object {
        lateinit var textArea: JTextArea

        @BeforeClass
        @JvmStatic
        fun setup() {
            textArea = JTextArea(" var  i ")
        }
    }

    @Test
    fun completeVar() {
        textArea.caretPosition = 4
        val completions = CompletionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("var"), completions.map { it.replacementText })
    }

    @Test
    fun completeAny() {
        textArea.caretPosition = 5
        val completions = CompletionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("(", ")", "*", "+", ",", "-", "->", "/", "=", "^", "out", "print", "var", "{", "}"),
                completions.map { it.replacementText })
    }
}
