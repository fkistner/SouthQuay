package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.interpreter.*

class Scope(val errorContainer: MutableList<SQLangError>) {
    constructor(scope: Scope): this(scope.errorContainer)

    val variables = mutableMapOf<String, VarDeclaration>()
    val functions = mapOf(
            Pair(FunctionSignature("map",    listOf(Type.Sequence, Type.Lambda(Type.Integer))), MapFunction),
            Pair(FunctionSignature("reduce", listOf(Type.Sequence, Type.Integer, Type.Lambda(Type.Integer))), ReduceFunction))
}
