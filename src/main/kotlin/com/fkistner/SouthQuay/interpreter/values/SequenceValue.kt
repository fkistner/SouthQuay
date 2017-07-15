package com.fkistner.SouthQuay.interpreter.values

import java.util.stream.*

class SequenceValue(val stream: () -> IntStream) {
    override fun toString(): String {
        val sizeIfKnown = stream().spliterator().exactSizeIfKnown
        if (sizeIfKnown < 0) throw IllegalStateException()

        val stringStream = { stream().mapToObj(Int::toString) }
        val displayStream = when (sizeIfKnown) {
            in 0..5 -> stringStream()
            else -> Stream.concat(Stream.concat(stringStream().limit(3), Stream.of("…")), stringStream().skip(sizeIfKnown - 1))
        }
        return "{${displayStream.collect(Collectors.joining(", "))}}"
    }
}