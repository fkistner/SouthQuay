package com.fkistner.SouthQuay.parser

data class Span(val start: Position, val end: Position)
data class Position(val line: Int, val column: Int, val index: Int)

