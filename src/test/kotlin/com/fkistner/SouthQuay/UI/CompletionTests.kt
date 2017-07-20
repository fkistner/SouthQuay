package com.fkistner.SouthQuay.UI

import org.junit.*
import javax.swing.JTextArea


class CompletionTests {

    companion object {
        val completionProposalGenerator = CompletionProposalGenerator()
        lateinit var textArea: JTextArea

        @BeforeClass
        @JvmStatic
        fun setup() {
            textArea = JTextArea()
        }
    }

    @Test
    fun completeVar() {
        textArea.text = " var  i "
        textArea.caretPosition = 4
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("var"), completions.map { it.toString() })
    }

    @Test
    fun completeFirstStatement() {
        textArea.text = "pr "
        textArea.caretPosition = 2
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("print"),
                completions.map { it.toString() })
    }

    @Test
    fun completeStatement() {
        textArea.text = "var aa = 5 out "
        textArea.caretPosition = 14
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("out"),
                completions.map { it.toString() })
    }

    @Test
    fun completeAnyStatement() {
        textArea.text = "var aa = 5 out "
        textArea.caretPosition = 11
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("*", "+", "-", "/", "^", "out", "print","var"),
                completions.map { it.toString() })
    }

    @Test
    fun completeExpression() {
        textArea.text = "  out 122  "
        textArea.caretPosition = 6
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("apply( - apply(number, lambda) function", "map( - map(sequence, lambda) function",
                "reduce( - reduce(sequence, neutral, lambda) function", "(", "-", "{"),
                completions.map { it.toString() })
    }

    @Test
    fun completeExpressionSeqVariable() {
        textArea.text = "var hello = {3, 8}  out "
        textArea.caretPosition = 24
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("hello - Sequence<Integer> variable", "apply( - apply(number, lambda) function",
                "map( - map(sequence, lambda) function", "reduce( - reduce(sequence, neutral, lambda) function",
                "(", "-", "{"),
                completions.map { it.toString() })
    }

    @Test
    fun completeExpressionPartialRealVariable() {
        textArea.text = "var test = 2.3  out te"
        textArea.caretPosition = 22
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("test - Real variable"),
                completions.map { it.toString() })
    }

    @Test
    fun completeNumber() {
        textArea.text = "  out 122  "
        textArea.caretPosition = 8
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(emptyList<String>(),
                completions.map { it.toString() })
    }

    @Test
    fun completeIdentifier() {
        textArea.text = "var aa = 5 out "
        textArea.caretPosition = 15
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("aa - Integer variable", "apply( - apply(number, lambda) function", "map( - map(sequence, lambda) function",
                "reduce( - reduce(sequence, neutral, lambda) function", "(", "-", "{"),
                completions.map { it.toString() })
    }

    @Test
    fun completeNewIdentifier() {
        textArea.text = "var n = 5 var  i"
        textArea.caretPosition = 14
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(emptyList<String>(),
                completions.map { it.toString() })
    }

    @Test
    fun completeAfterNewIdentifier() {
        textArea.text = "var n = 5 var  i ="
        textArea.caretPosition = 18
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("n - Integer variable", "apply( - apply(number, lambda) function",
                "map( - map(sequence, lambda) function", "reduce( - reduce(sequence, neutral, lambda) function", "(", "-", "{"),
                completions.map { it.toString() })
    }

    @Test
    fun completeSequenceAfterCurlyOpen() {
        textArea.text = "out {1, a}"
        textArea.caretPosition = 5
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("apply( - apply(number, lambda) function", "map( - map(sequence, lambda) function",
                "reduce( - reduce(sequence, neutral, lambda) function", "(", "-", "{"),
                completions.map { it.toString() })
    }

    @Test
    fun completeSequenceBeforeComma() {
        textArea.text = "out {1 , a}"
        textArea.caretPosition = 7
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("*", "+", ",", "-", "/", "^"),
                completions.map { it.toString() })
    }

    @Test
    fun completeSequenceBeforeCurly() {
        textArea.text = "out {1, a }"
        textArea.caretPosition = 10
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("*", "+", "-", "/", "^","}"),
                completions.map { it.toString() })
    }

    @Test
    fun completeInLambda() {
        textArea.text = "var n = 2*20 var s10 = map({i10, 10*i10}, i -> i*i)"
        textArea.caretPosition = 47
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(listOf("i - lambda parameter", "apply( - apply(number, lambda) function",
                "map( - map(sequence, lambda) function", "reduce( - reduce(sequence, neutral, lambda) function", "(", "-", "{"),
                completions.map { it.toString() })
    }

    @Test
    fun completeInLambdaMissingParam() {
        textArea.text = "var n = 2*20 var s10 = map({i10, 10*i10}, -> i*i)"
        textArea.caretPosition = 45
        val completions = completionProposalGenerator.getCompletions(textArea)

        Assert.assertEquals(emptyList<String>(),
                completions.map { it.toString() })
    }
}
