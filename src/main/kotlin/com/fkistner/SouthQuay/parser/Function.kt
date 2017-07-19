package com.fkistner.SouthQuay.parser

/** Interface function implementation that provides information to the type system. */
interface Function {
    /**
     * Determine type the function invocation resolves to based on its arguments.
     * @param invocation Function invocation targeting this function
     * @param type Return type of the function
     */
    fun resolve(invocation: FunctionInvoc): Type

    /**
     * Verifies that the types of the arguments match the function.
     * @param argumentTypes List of argument types
     * @return `true` if argument types are valid for calling function.
     */
    fun verify(argumentTypes: List<Type>): Boolean
}
