package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.*

object ApplyFunction : TypedInvocableFunction {
    override fun resolve(invocation: FunctionInvoc): Type {
        val lambda = invocation.args[1] as Lambda
        lambda.parameters[0].type = invocation.args[0].type
        return lambda.body.type
    }

    override fun invoke(invocation: FunctionInvoc, args: List<Any?>): Any? {
        val lambda = args[1] as InvocableLambda?
        return args[0]?.let { lambda?.invoke(listOf(it)) }
    }
}
