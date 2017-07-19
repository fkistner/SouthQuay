package com.fkistner.SouthQuay.interpreter

/** Interface for an interpreter implementation that is dependent on the execution context. */
interface ContextualInterpreter {
    /** Current execution context. */
    val context: ExecutionContext
}
