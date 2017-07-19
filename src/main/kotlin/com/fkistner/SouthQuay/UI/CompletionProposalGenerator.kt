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
        val preText = comp.document.getText(0, caretPosition)

        val charStream = CharStreams.fromReader(StringReader(preText))
        val lexer = CaretAwareLexer(charStream, caretPosition)

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

    /**
     * Special purpose lexer that inserts a special caret token to avoid matching EOF as valid token.
     *
     * Necessary to get parser information if [caretPosition] is a statement boundary.
     * @param charStream Character stream of the program up until [caretPosition]
     * @param caretPosition Position of the caret
     */
    class CaretAwareLexer(charStream: CodePointCharStream, val caretPosition: Int): SQLangLexer(charStream) {
        /** Cache for EOF token while caret token is returned */
        var eofToken: Token? = null

        override fun nextToken(): Token? {
            eofToken?.let { return eofToken }
            var token = super.nextToken()
            if (token?.type == EOF) {
                eofToken = token
                token = CommonToken(_tokenFactorySourcePair, -10, DEFAULT_TOKEN_CHANNEL, caretPosition, caretPosition)
                token.text = "CARET"
            }
            return token
        }
    }

    /**
     * Creates a completion model object for referencing a lambda parameter.
     * @param token The token that declared the lambda parameter
     * @return Completion for the lambda parameter
     */
    fun lambdaCompletion(token: Token) = BasicCompletion(this, token.text, "lambda parameter")

    /**
     * Creates a completion model object for referencing a variable.
     * @param declaration The node that declared the variable
     * @return Completion for the variable
     */
    fun variableCompletion(declaration: VarDeclaration) =
            BasicCompletion(this, declaration.identifier, "${declaration.type} variable")

    /**
     * Creates a completion model object for invoking a function.
     * @param signature The signature of the function
     * @return Completion for the function
     */
    fun functionCompletion(signature: FunctionSignature) =
            BasicCompletion(this, "${signature.identifier}(",
                    "${signature.identifier}(${signature.argumentNames.joinToString()}) function")

    /**
     * Determines which variables, parameters, and functions can be referenced at the current position and creates corresponding completions.
     * @param program The partial abstract syntax tree recovered from the source up to the [caretPosition]
     * @param ruleContext Current rule context of the parser
     * @param caretPosition Position of the user's caret
     * @return Sequence of completions for valid identifiers at this point
     */
    fun getIdentifierCompletion(program: Program, ruleContext: ParserRuleContext, caretPosition: Int): Sequence<BasicCompletion> {
        if (ruleContext is SQLangParser.VarContext) return emptySequence()

        val lambdaCompletions = generateSequence(ruleContext, ParserRuleContext::getParent)
                .mapNotNull { it as? SQLangParser.LamContext }
                .firstOrNull()?.let {
                    it.params?.asSequence()
                            ?.map(this::lambdaCompletion)
                                    ?: emptySequence()
                }
        val variableCompletions = program.scope.variables.values.asSequence()
                .filter { it.span.end.index <= caretPosition }
                .map(this::variableCompletion)
        val functionCompletions = program.scope.functions.keys.asSequence()
                .map(this::functionCompletion)

        return (lambdaCompletions ?: variableCompletions).onEach { it.relevance = 2 } +
                functionCompletions.onEach { it.relevance = 1 }
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
