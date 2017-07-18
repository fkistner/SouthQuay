package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.grammar.*
import com.fkistner.SouthQuay.grammar.SQLangLexer.*
import com.fkistner.SouthQuay.parser.*
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.*
import org.antlr.v4.runtime.tree.TerminalNode
import org.fife.ui.autocomplete.*
import java.io.StringReader
import javax.swing.text.JTextComponent
import kotlin.sequences.Sequence
import kotlin.Pair


class CompletionProposalGenerator : DefaultCompletionProvider() {
    override fun getCompletionsImpl(comp: JTextComponent): MutableList<Completion>? {
        completions.clear()

        val caretPosition = comp.caretPosition
        var lastChar = caretPosition - 1
        while (lastChar >= 0 && comp.document.getText(lastChar, 1)[0].isLetterOrDigit()) lastChar--

        val preText = comp.document.getText(0, lastChar + 1)

        val charStream = CharStreams.fromReader(StringReader(preText))
        val lexer = object: SQLangLexer(charStream) {
            var eofToken: Token? = null

            override fun nextToken(): Token? {
                eofToken?.let { return eofToken }
                var token = super.nextToken()
                if (token?.type == EOF) {
                    eofToken = token
                    token = CommonToken(_tokenFactorySourcePair, -10, DEFAULT_TOKEN_CHANNEL, lastChar, lastChar)
                    token.text = "CARET"
                }
                return token
            }
        }

        lexer.removeErrorListeners()
        val parser = SQLangParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()

        val expectedTokens = mutableListOf<Pair<List<Interval>, ParserRuleContext>>()
        parser.addErrorListener(object: BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                if ((offendingSymbol as? Token)?.type != -10) return
                expectedTokens.add(parser.expectedTokens.intervals.to(parser.ruleContext))
            }
        })

        val program = parser.program().toAST()

        for ((intervals, ruleContext) in expectedTokens) {
            intervals.asSequence()
                    .flatMap { (it.a..it.b).asSequence() }
                    .flatMap { getTokenCompletion(program, ruleContext, caretPosition, it) }
                    .sorted()
                    .toCollection(completions)
        }

        return super.getCompletionsImpl(comp)
    }

    fun variableCompletion(completionProvider: CompletionProvider, declaration: VarDeclaration) =
            BasicCompletion(completionProvider, declaration.identifier, "${declaration.type} variable")

    fun functionCompletion(completionProvider: CompletionProvider, signature: FunctionSignature) =
            BasicCompletion(completionProvider, "${signature.identifier}(",
                    "${signature.identifier}(${signature.argumentNames.joinToString()}) function")

    fun getIdentifierCompletion(program: Program, ruleContext: ParserRuleContext, caretPosition: Int): Sequence<BasicCompletion> {
        if (ruleContext is SQLangParser.VarContext) return emptySequence()

        val variableCompletions = program.scope.variables.values.asSequence()
                .filter { it.span.end.index <= caretPosition }
                .map { variableCompletion(this, it) }
        val functionCompletions = program.scope.functions.keys.asSequence()
                .map { functionCompletion(this, it) }

        return (variableCompletions + functionCompletions).onEach { it.relevance = 1 }
    }

    private fun getTokenCompletion(program: Program, ruleContext: ParserRuleContext,
                                   caretPosition: Int, token: Int): Sequence<BasicCompletion> {
        when (token) {
            Identifier -> return getIdentifierCompletion(program, ruleContext, caretPosition)
            EOF, Whitespace -> return emptySequence()
        }
        return VOCABULARY.getLiteralName(token)?.let {
            val literalToken = it.trim('\'')
            sequenceOf(BasicCompletion(this, literalToken))
        } ?: emptySequence()
    }
}
