package com.fkistner.SouthQuay.interpreter.values

import java.util.stream.*

/** Super class for all sequence value containers. */
sealed class SequenceValue<out T: Number> {
    /** Stream provider as basis for operations. */
    abstract val stream: () -> BaseStream<out T, *>

    /**
     * Maps sequence to integer sequence.
     * @param mapper Stateless function that is applied to every element
     * @return Integer sequence
     * @throws IllegalStateException if operation cannot be applied upon sequence type
     */
    abstract fun map(mapper: (T) -> Int):    IntSequenceValue

    /**
     * Maps sequence to real sequence.
     * @param mapper Stateless function that is applied to every element
     * @return Real sequence
     */
    abstract fun map(mapper: (T) -> Double): RealSequenceValue

    /**
     * Reduces elements of the sequence to an integer value.
     * @param neutral Neutral element, which may be applied without affect on the result
     * @param reducer Associative, stateless function that combines two values
     * @return Integer value
     * @throws IllegalStateException if operation cannot be applied upon sequence type
     */
    abstract fun reduce(neutral: Int, reducer: (Int, Int) -> Int): Int

    /**
     * Reduces elements of the sequence to a real value.
     * @param neutral Neutral element, which may be applied without affect on the result
     * @param reducer Associative, stateless function that combines two values
     * @return Real value
     */
    abstract fun reduce(neutral: Double, reducer: (Double, Double) -> Double): Double

    /**
     * Transform the sequence into a stream of strings.
     * @return Stream of string representations
     */
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

/**
 * Integer sequence value container.
 * @param stream Integer stream provider as basis for operations
 */
class IntSequenceValue(override val stream: () -> IntStream): SequenceValue<Int>() {
    override fun map(mapper: (Int) -> Int)    = IntSequenceValue  { stream().parallel().map(mapper) }
    override fun map(mapper: (Int) -> Double) = RealSequenceValue { stream().parallel().mapToDouble(mapper) }
    override fun reduce(neutral: Int, reducer: (Int, Int) -> Int)             = stream().parallel().reduce(neutral, reducer)
    override fun reduce(neutral: Double, reducer: (Double, Double) -> Double) = stream().parallel().asDoubleStream().reduce(neutral, reducer)

    override fun asStringStream(): Stream<String> = stream().mapToObj(Int::toString)
}

/**
 * Real sequence value container.
 * @param stream Real stream provider as basis for operations
 */
class RealSequenceValue(override val stream: () -> DoubleStream): SequenceValue<Double>() {
    override fun map(mapper: (Double) -> Int)    = throw IllegalStateException()
    override fun map(mapper: (Double) -> Double) = RealSequenceValue { stream().parallel().map(mapper) }
    override fun reduce(neutral: Int, reducer: (Int, Int) -> Int)          = throw IllegalStateException()
    override fun reduce(neutral: Double, reducer: (Double, Double) -> Double) = stream().parallel().reduce(neutral, reducer)

    override fun asStringStream(): Stream<String> = stream().mapToObj(Double::toString)
}
