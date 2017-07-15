package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.interpreter.functions.*
import com.fkistner.SouthQuay.parser.*


object FunctionRegistry {
    val functions = mapOf<FunctionSignature, TypedInvocableFunction>(
            Pair(FunctionSignature("apply",  listOf(Type.Integer, Type.Lambda)), ApplyFunction),
            Pair(FunctionSignature("map",    listOf(Type.Sequence(Type.Integer), Type.Lambda)), MapFunction),
            Pair(FunctionSignature("reduce", listOf(Type.Sequence(Type.Integer), Type.Integer, Type.Lambda)), ReduceFunction))
}