package com.fkistner.SouthQuay.interpreter.values

import java.util.stream.*

class IntSequenceValue(override val stream: () -> IntStream): SequenceValue<Int>() {
    override fun map(mapper: (Int) -> Int)    = IntSequenceValue  { stream().map(mapper) }
    override fun map(mapper: (Int) -> Double) = RealSequenceValue { stream().mapToDouble(mapper) }
    override fun reduce(initial: Int,    reducer: (Int, Int) -> Int)          = stream().reduce(initial, reducer)
    override fun reduce(initial: Double, reducer: (Double, Double) -> Double) = stream().asDoubleStream().reduce(initial, reducer)

    override fun asStringStream(): Stream<String> = stream().mapToObj(Int::toString)
}