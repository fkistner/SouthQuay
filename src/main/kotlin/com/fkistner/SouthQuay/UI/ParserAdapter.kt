package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.document.text
import com.fkistner.SouthQuay.parser.*
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.parser.*
import kotlin.system.measureTimeMillis

/**
 * Provides code validation for [RSyntaxDocument]s to be display in the code editor.
 */
class ParserAdapter: AbstractParser() {
    /** Additional errors, such as [RuntimeError]s, to be displayed in code. */
    var additionalErrors = mutableListOf<SQLangError>()

    override fun parse(doc: RSyntaxDocument, style: String): ParseResult {
        val result = DefaultParseResult(this)

        var parseResult: Pair<Program?, MutableList<SQLangError>>? = null
        result.parseTime = measureTimeMillis {
            parseResult = ASTBuilder.parseText(doc.text)
        }
        val (_, errors) = parseResult!!
        errors.map { it.toNotice() }.forEach(result::addNotice)
        additionalErrors.map { it.toNotice() }.forEach(result::addNotice)

        result.setParsedLines(0, doc.defaultRootElement.elementCount)
        return result
    }

    /**
     * Translates error into a parser notice.
     * @return [ParserNotice]
     */
    fun SQLangError.toNotice(): DefaultParserNotice {
        val notice = DefaultParserNotice(this@ParserAdapter, message, span.start.line - 1,
                span.start.index, span.length)
        notice.level = ParserNotice.Level.ERROR
        return notice
    }
}