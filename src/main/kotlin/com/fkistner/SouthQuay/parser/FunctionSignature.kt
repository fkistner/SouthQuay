package com.fkistner.SouthQuay.parser

/**
 * Function signature used to resolve function invocation targets.
 * @param identifier Name of the function
 * @param argumentCount Number of arguments
 */
data class FunctionSignature(val identifier: String, val argumentCount: Int) {
    /** Names of the function arguments. */
    lateinit var argumentNames: List<String>

    /**
     * Function signature used to resolve function invocation targets.
     *
     * This secondary constructor initializes the [argumentNames].
     * @param identifier Name of the function
     * @param argumentNames Names of the function arguments
     */
    constructor(identifier: String, argumentNames: List<String>): this(identifier, argumentNames.size) {
        this.argumentNames = argumentNames
    }
}
