package com.fkistner.SouthQuay

import java.io.*

/**
 * Measures the performance of the given action with nano seconds precision and forwards its return value alongside.
 *
 * @param input Input to [action]
 * @param action Action to time
 * @return Result of action and elapsed time in seconds with nano second precision
 * @see kotlin.system.measureNanoTime
 */
inline fun <T,R>measurePerformance(input: T, action: (T) -> R): Pair<R, Double> {
    val start = System.nanoTime()
    val result = action(input)
    val elapsed = System.nanoTime() - start
    return Pair(result, elapsed / 1.0e9)
}

/**
 * Measures the performance of the given action with nano seconds precision.
 *
 * @param input Input to [action]
 * @param action Action to time
 * @return Elapsed time in seconds with nano second precision
 * @see kotlin.system.measureNanoTime
 */
inline fun <T>measurePerformance(input: T, action: (T) -> Unit): Double {
    val start = System.nanoTime()
    action(input)
    val elapsed = System.nanoTime() - start
    return elapsed / 1.0e9
}

/**
 * Extracts the complete exception details.
 * @return Stack trace as string
 */
fun Throwable.toFullString(): String {
    val writer = StringWriter()
    printStackTrace(PrintWriter(writer))
    return writer.buffer.toString()
}
