package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*

/**
 * Executes provided action and records runtime errors occurred in the execution context.
 * @param node Node currently being interpreted
 * @param action Action to be executed and may throw a [Throwable]
 * @see ExecutionContext
 */
internal inline fun <T> ContextualInterpreter.trackError(node: ASTNode, action: () -> T): T {
    try {
        return action()
    } catch (t: Throwable) {
        if (context.runtimeError == null)
            context.runtimeError = RuntimeError(t, node)
        throw t
    }
}
