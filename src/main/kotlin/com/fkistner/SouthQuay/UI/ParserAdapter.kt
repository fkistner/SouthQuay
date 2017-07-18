package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.document.text
import com.fkistner.SouthQuay.parser.*
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.parser.*
import kotlin.system.measureTimeMillis


class ParserAdapter: AbstractParser() {
    var additionalErrors = mutableListOf<SQLangError>()

    override fun parse(doc: RSyntaxDocument, style: String): ParseResult {
        val result = DefaultParseResult(this)

        var parseResult: Pair<Program?, MutableList<SQLangError>>? = null
        result.parseTime = measureTimeMillis {
            parseResult = ASTBuilder.parseText(doc.text)
        }
        val (_, errors) = parseResult!!
        errors.map(this::toNotice).forEach(result::addNotice)
        additionalErrors.map(this::toNotice).forEach(result::addNotice)

        result.setParsedLines(0, doc.defaultRootElement.elementCount)
        return result
    }

    fun toNotice(error: SQLangError): DefaultParserNotice {
        val notice = DefaultParserNotice(this, error.message, error.span.start.line - 1,
                error.span.start.index, error.span.length)
        notice.level = ParserNotice.Level.ERROR
        return notice
    }
}