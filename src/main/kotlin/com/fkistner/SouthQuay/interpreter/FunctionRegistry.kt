package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.interpreter.functions.*

/** Registry for all invocable functions. */
object FunctionRegistry {
    /** List of invocable functions. */
    val functions = listOf(ApplyFunction, MapFunction, ReduceFunction)
            .asSequence().map { it.signature.to(it) }.toMap()
}