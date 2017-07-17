package com.fkistner.SouthQuay.parser

data class Span(val start: Position, val end: Position) {
    val length
        get() = end.index - start.index

    constructor(start: Position, offset: Int): this(start, start + offset)
}

data class Position(val line: Int, val column: Int, val index: Int) {
    operator fun plus(offset: Int) = Position(line, column + offset, index + offset)
}
