package com.fkistner.SouthQuay.interpreter.values

import java.util.stream.*

class RealSequenceValue(override val stream: () -> DoubleStream): SequenceValue<Double>() {
    override fun map(mapper: (Double) -> Int)    = throw IllegalStateException()
    override fun map(mapper: (Double) -> Double) = RealSequenceValue { stream().map(mapper) }
    override fun reduce(initial: Int,    reducer: (Int, Int) -> Int)          = throw IllegalStateException()
    override fun reduce(initial: Double, reducer: (Double, Double) -> Double) = stream().reduce(initial, reducer)

    override fun asStringStream(): Stream<String> = stream().mapToObj(Double::toString)
}