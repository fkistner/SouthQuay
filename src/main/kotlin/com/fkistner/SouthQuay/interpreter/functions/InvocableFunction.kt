package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.*
import com.fkistner.SouthQuay.parser.Function

/** Interface for a lambda that can be invoked with a list of parameters. */
interface InvocableLambda: (List<Any?>) -> Any?

/** Interface for a function that can be invoked with its call site and a list of parameters. */
interface InvocableFunction: (FunctionInvoc, List<Any?>) -> Any?

/**
 * Interface for a function that can be invoked with its call site and a list of parameters,
 * and can provide type information and its signature.
 */
interface TypedInvocableFunction: InvocableFunction, Function {
    /** Signature of the function. */
    val signature: FunctionSignature
}
