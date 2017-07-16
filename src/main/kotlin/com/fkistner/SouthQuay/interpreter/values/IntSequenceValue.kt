package com.fkistner.SouthQuay.interpreter.values

import java.util.stream.*

class IntSequenceValue(override val stream: () -> IntStream): SequenceValue<Int>() {
    override fun map(mapper: (Int) -> Int)    = IntSequenceValue  { stream().parallel().map(mapper) }
    override fun map(mapper: (Int) -> Double) = RealSequenceValue { stream().parallel().mapToDouble(mapper) }
    override fun reduce(initial: Int,    reducer: (Int, Int) -> Int)          = IntStream.concat(IntStream.of(initial), stream().parallel()).reduce(reducer).asInt
    override fun reduce(initial: Double, reducer: (Double, Double) -> Double) = DoubleStream.concat(DoubleStream.of(initial), stream().parallel().asDoubleStream()).reduce(reducer).asDouble

    override fun asStringStream(): Stream<String> = stream().mapToObj(Int::toString)
}