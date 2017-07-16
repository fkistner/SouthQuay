package com.fkistner.SouthQuay.interpreter.values

import java.util.stream.*

abstract class SequenceValue<out T: Number> {
    abstract val stream: () -> BaseStream<out T, *>
    abstract fun map(mapper: (T) -> Int):    IntSequenceValue
    abstract fun map(mapper: (T) -> Double): RealSequenceValue
    abstract fun reduce(initial: Int, reducer: (Int, Int) -> Int): Int
    abstract fun reduce(initial: Double, reducer: (Double, Double) -> Double): Double

    protected abstract fun asStringStream(): Stream<String>

    override fun toString(): String {
        val sizeIfKnown = stream().spliterator().exactSizeIfKnown
        if (sizeIfKnown < 0) throw IllegalStateException()

        val collector = Collectors.joining(", ")
        val innerPart = when (sizeIfKnown) {
            in 0..5 -> asStringStream().collect(collector)
            else -> "${asStringStream().limit(3).collect(collector)}, …, ${asStringStream().last}"
        }
        return "{$innerPart}"
    }
}