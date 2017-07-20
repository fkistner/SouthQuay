package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.grammar.*
import com.fkistner.SouthQuay.grammar.SQLangLexer.*
import com.fkistner.SouthQuay.parser.*
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.*
import org.fife.ui.autocomplete.*
import java.io.StringReader
import javax.swing.text.JTextComponent
import kotlin.sequences.Sequence
import kotlin.Pair


/**
 * Generates auto completion proposals based on parser state at the caret position and correctly parsed statements
 * to be displayed in the UI by an [AutoCompletion] controller.
 *
 * Instances are not shared between editors.
 */
class CompletionProposalGenerator : DefaultCompletionProvider() {
    companion object {
        /** Type of synthetic CARET token */
        const val CARET = -10

        /** Display name for debugging of CARET token */
        const val CARET_NAME = "CARET"
    }

    override fun getCompletionsImpl(comp: JTextComponent): MutableList<Completion>? {
        completions.clear()

        /** Back track if were are within something which looks like an identifier or literal to match completion logic
         * @see [getAlreadyEnteredText]
         * @see [isValidChar]
         */
        var lastChar = comp.caretPosition - 1
        while (lastChar >= 0 && isValidChar(comp.document.getText(lastChar, 1)[0])) lastChar--

        // Assume beginning of text as caret position
        val caretPosition = lastChar + 1

        val charStream = CharStreams.fromReader(StringReader(comp.text))
        val lexer = CaretAwareLexer(charStream, caretPosition)
        lexer.removeErrorListeners()
        val parser = SQLangParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()

        val expectedTokens = mutableListOf<Pair<List<Interval>, ParserRuleContext>>()
        parser.errorHandler = object: DefaultErrorStrategy() {
            /**
             * Checks for the CARET token. If found, the parser state is queried for expected tokens at this point,
             * which are stored for the generation of completion proposals, and the CARET token is consumed.
             * @param recognizer The parser to query and affect
             * @return Expected tokens, if CARET found, otherwise `null`
             */
            fun checkForCaret(recognizer: Parser): IntervalSet? {
                val inputStream = recognizer.inputStream
                return if (inputStream.LA(1) == CARET) recognizer.expectedTokens.also {
                    expectedTokens.add(it.intervals.to(recognizer.ruleContext))
                    inputStream.consume()
                } else null
            }

            override fun sync(recognizer: Parser) {
                checkForCaret(recognizer)
                super.sync(recognizer)
            }

            override fun recover(recognizer: Parser, e: RecognitionException) {
                checkForCaret(recognizer) ?: super.recover(parser, e)
            }

            override fun recoverInline(recognizer: Parser): Token {
                checkForCaret(recognizer)?.let {
                    val currentToken = recognizer.currentToken
                    if (it.contains(currentToken.type)) return currentToken
                }
                return super.recoverInline(recognizer)
            }
        }

        val program = parser.program().toAST()
        val proposals = mutableSetOf<CompletionProposal>()

        for ((intervals, ruleContext) in expectedTokens) {
            intervals.asSequence()
                    .flatMap { (it.a..it.b).asSequence() }
                    .flatMap { getTokenCompletion(program, ruleContext, caretPosition, it) }
                    .toCollection(proposals)
        }

        proposals.asSequence().map { it.asCompletion(this) }.sorted().toCollection(completions)
        return super.getCompletionsImpl(comp)
    }

    /**
     * Special purpose lexer that inserts a special caret token, which can be reacted to.
     *
     * Necessary to get parser information if [caretPosition] is a statement boundary.
     * @param charStream Character stream of the program up until [caretPosition]
     * @param caretPosition Position of the caret
     */
    class CaretAwareLexer(charStream: CodePointCharStream, val caretPosition: Int): SQLangLexer(charStream) {
        /** Cache next token while caret token is returned. */
        var nextToken: Token? = null

        override fun nextToken(): Token? {
            nextToken?.let {
                nextToken = null
                return it
            }
            var token = super.nextToken()
            if (token != null && token.startIndex == caretPosition || (token.startIndex < caretPosition && caretPosition <= token.stopIndex)) {
                nextToken = token
                token = CommonToken(_tokenFactorySourcePair, CARET, DEFAULT_TOKEN_CHANNEL, caretPosition, caretPosition - 1)
                token.text = CARET_NAME
            }
            return token
        }
    }

    /** Super class for all completion proposal, which might be suggested to the user. */
    sealed class CompletionProposal {
        /**
         * Creates a completion model object of this proposal for the given completion provider.
         * @param completionProvider The completion provider the completions should be registered with
         * @return Completion model object for display in the UI
         */
        abstract fun asCompletion(completionProvider: CompletionProvider): Completion

        /**
         * Completion proposal for referencing a lambda parameter.
         * @param token The token that declared the lambda parameter
         */
        data class Lambda(val token: org.antlr.v4.runtime.Token): CompletionProposal() {
            override fun asCompletion(completionProvider: CompletionProvider)
                    = BasicCompletion(completionProvider, token.text, "lambda parameter").also { it.relevance = 2 }
        }

        /**
         * Completion proposal for referencing a variable.
         * @param declaration The node that declared the variable
         */
        data class Variable(val declaration: VarDeclaration): CompletionProposal() {
            override fun asCompletion(completionProvider: CompletionProvider)
                    = BasicCompletion(completionProvider, declaration.identifier, "${declaration.type} variable").also { it.relevance = 2 }
        }

        /**
         * Completion proposal for invoking a function.
         * @param signature The signature of the function
         */
        data class Function(val signature: FunctionSignature): CompletionProposal() {
            override fun asCompletion(completionProvider: CompletionProvider)
                    = BasicCompletion(completionProvider, "${signature.identifier}(",
                    "${signature.identifier}(${signature.argumentNames.joinToString()}) function").also { it.relevance = 1 }
        }

        /**
         * Completion proposal for a possible literal token.
         * @param literalToken String representation of the token
         */
        data class Token(val literalToken: String): CompletionProposal() {
            override fun asCompletion(completionProvider: CompletionProvider)
                    = BasicCompletion(completionProvider, literalToken)
        }
    }

    /**
     * Determines which variables, parameters, and functions can be referenced at the current position and creates corresponding completions.
     * @param program The partial abstract syntax tree recovered from the source up to the [caretPosition]
     * @param ruleContext Current rule context of the parser
     * @param caretPosition Position of the user's caret
     * @return Sequence of completions for valid identifiers at this point
     */
    fun getIdentifierCompletion(program: Program, ruleContext: ParserRuleContext, caretPosition: Int): Sequence<CompletionProposal> {
        if (ruleContext is SQLangParser.VarContext) return emptySequence()

        val lambdaCompletions = generateSequence(ruleContext, ParserRuleContext::getParent)
                .mapNotNull { it as? SQLangParser.LamContext }
                .firstOrNull()?.let {
                    it.params?.asSequence()
                            ?.map(CompletionProposal::Lambda)
                                    ?: emptySequence()
                }
        val variableCompletions = program.scope.variables.values.asSequence()
                .filter { it.span.end.index <= caretPosition }
                .map(CompletionProposal::Variable)
        val functionCompletions = program.scope.functions.keys.asSequence()
                .map(CompletionProposal::Function)

        return (lambdaCompletions ?: variableCompletions) + functionCompletions
    }

    /**
     * Creates completions for the expected parser [token].
     * @param program The partial abstract syntax tree recovered from the source up to the [caretPosition]
     * @param ruleContext Current rule context of the parser
     * @param caretPosition Position of the user's caret
     * @param token Token expected to succeed the caret
     * @return Sequence of completions for valid identifiers at this point
     */
    fun getTokenCompletion(program: Program, ruleContext: ParserRuleContext,
                                   caretPosition: Int, token: Int): Sequence<CompletionProposal> {
        when (token) {
            Identifier -> return getIdentifierCompletion(program, ruleContext, caretPosition)
            EOF, Whitespace -> return emptySequence()
        }
        return VOCABULARY.getLiteralName(token)?.let {
            val literalToken = it.trim('\'')
            sequenceOf<CompletionProposal>(CompletionProposal.Token(literalToken))
        } ?: emptySequence()
    }
}
