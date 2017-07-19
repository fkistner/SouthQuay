package com.fkistner.SouthQuay.parser

/**
 * Span of text in source.
 * @param start Beginning of span (indexes are inclusive)
 * @param end End of span (indexes are exclusive)
 */
data class Span(val start: Position, val end: Position) {
    val length
        get() = end.index - start.index

    constructor(start: Position, offset: Int): this(start, start + offset)
}

/**
 * Position in source.
 * @param line Line number (starting with 1)
 * @param column Number of characters from the start of the line (starting with 0)
 * @param index Number of characters from the start of the text (starting with 0)
 */
data class Position(val line: Int, val column: Int, val index: Int) {
    operator fun plus(offset: Int) = Position(line, column + offset, index + offset)
}
