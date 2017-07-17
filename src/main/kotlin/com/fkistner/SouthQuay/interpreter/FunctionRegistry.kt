package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.interpreter.functions.*


object FunctionRegistry {
    val functions = listOf(ApplyFunction, MapFunction, ReduceFunction)
            .asSequence().map { it.signature.to(it) }.toMap()
}