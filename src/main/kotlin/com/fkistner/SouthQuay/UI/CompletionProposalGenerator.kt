package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.grammar.SQLangLexer
import com.fkistner.SouthQuay.grammar.SQLangLexer.VOCABULARY
import org.fife.ui.autocomplete.*


object CompletionProposalGenerator: DefaultCompletionProvider() {

    init {
        completions = (1..SQLangLexer.VOCABULARY.maxTokenType).asSequence().mapNotNull { i ->
            SQLangLexer.VOCABULARY.getLiteralName(i)?.let {
                val literalToken = it.trim('\'')
                BasicCompletion(this, literalToken, VOCABULARY.getSymbolicName(i))
            }
        }.sorted().toList()
    }

}
