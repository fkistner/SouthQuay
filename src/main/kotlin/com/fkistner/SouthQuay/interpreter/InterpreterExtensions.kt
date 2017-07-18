package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*


internal inline fun <T> ContextualInterpreter.trackError(node: ASTNode, action: () -> T): T {
    try {
        return action()
    } catch (t: Throwable) {
        if (context.runtimeError == null)
            context.runtimeError = RuntimeError(t.localizedMessage, node)
        throw t
    }
}