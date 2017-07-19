package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.interpreter.FunctionRegistry

/**
 * Variable and function scope that determines visibility of identifiers.
 * @param errorContainer Error container associated with this scope
 */
class Scope(val errorContainer: MutableList<SQLangError>) {
    /**
     * Variable and function scope that determines visibility of identifiers.
     *
     * This secondary constructor creates a scope with the same error container.
     * @param scope Scope error container is taken from
     */
    constructor(scope: Scope): this(scope.errorContainer)

    /** Variables visible in scope. */
    val variables = mutableMapOf<String, VarDeclaration>()

    /** Functions visible in scope (provided by the [FunctionRegistry]). */
    val functions: Map<FunctionSignature, Function> get() = FunctionRegistry.functions
}
