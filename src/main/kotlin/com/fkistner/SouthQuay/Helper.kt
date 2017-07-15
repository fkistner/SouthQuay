package com.fkistner.SouthQuay


inline fun <T,R>measurePerformance(input: T, action: (T) -> R): Pair<R, Double> {
    val start = System.nanoTime()
    val result = action(input)
    val elapsed = System.nanoTime() - start
    return result.to(elapsed / 1.0e9)
}