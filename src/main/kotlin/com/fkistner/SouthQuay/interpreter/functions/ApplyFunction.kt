package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.*

object ApplyFunction : TypedInvocableFunction {
    override val signature = FunctionSignature("apply", listOf("number", "lambda"))

    override fun resolve(invocation: FunctionInvoc): Type {
        (invocation.args[1] as? Lambda)?.let { (parameters, body) ->
            parameters[0].type = invocation.args[0].type
            return body.type
        }
        return Type.Error
    }

    override fun verify(argumentTypes: List<Type>) = (argumentTypes[0] == Type.Integer || argumentTypes[0] == Type.Real)
            && argumentTypes[1] == Type.Lambda

    override fun invoke(invocation: FunctionInvoc, args: List<Any?>): Any? {
        val lambda = args[1] as InvocableLambda?
        return args[0]?.let { lambda?.invoke(listOf(it)) }
    }
}
