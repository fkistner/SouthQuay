package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*

/** Context that holds all active values and error information. */
class ExecutionContext {
    /** Values that have been stored by variable declarations or parameter instantiations. */
    val activeValues = mutableMapOf<VarDeclaration, Any?>()

    /** Runtime errors that has occurred during execution. */
    var runtimeError: RuntimeError? = null
}