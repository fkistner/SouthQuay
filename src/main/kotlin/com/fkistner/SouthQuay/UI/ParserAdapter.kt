package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.document.text
import com.fkistner.SouthQuay.parser.*
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.parser.*
import kotlin.system.measureTimeMillis


object ParserAdapter: AbstractParser() {
    var additionalErrors = mutableListOf<SQLangError>()

    override fun parse(doc: RSyntaxDocument, style: String): ParseResult {
        val result = DefaultParseResult(this)

        var parseResult: Pair<Program?, MutableList<SQLangError>>? = null
        result.parseTime = measureTimeMillis {
            parseResult = ASTBuilder.parseText(doc.text)
        }
        val (program, errors) = parseResult!!
        errors.map(this::toNotice).forEach(result::addNotice)
        additionalErrors.map(this::toNotice).forEach(result::addNotice)

        program?.let {
            result.setParsedLines(it.span.start.line, it.span.end.line)
        }

        return result
    }

    fun toNotice(error: SQLangError): DefaultParserNotice {
        val notice = DefaultParserNotice(this, error.message, error.span.start.line - 1,
                error.span.start.index, error.span.length)
        notice.level = ParserNotice.Level.ERROR
        return notice
    }
}