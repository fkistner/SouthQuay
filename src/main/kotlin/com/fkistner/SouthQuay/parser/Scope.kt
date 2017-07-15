package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.interpreter.FunctionRegistry

class Scope(val errorContainer: MutableList<SQLangError>) {
    constructor(scope: Scope): this(scope.errorContainer)

    val variables = mutableMapOf<String, VarDeclaration>()
    val functions: Map<FunctionSignature, Function> = FunctionRegistry.functions
}
