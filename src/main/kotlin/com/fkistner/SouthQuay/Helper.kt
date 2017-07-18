package com.fkistner.SouthQuay


inline fun <T,R>measurePerformance(input: T, action: (T) -> R): Pair<R, Double> {
    val start = System.nanoTime()
    val result = action(input)
    val elapsed = System.nanoTime() - start
    return Pair(result, elapsed / 1.0e9)
}

inline fun <T>measurePerformance(input: T, action: (T) -> Unit): Double {
    val start = System.nanoTime()
    action(input)
    val elapsed = System.nanoTime() - start
    return elapsed / 1.0e9
}
