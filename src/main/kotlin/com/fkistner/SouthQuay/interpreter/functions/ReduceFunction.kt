package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.interpreter.values.SequenceValue
import com.fkistner.SouthQuay.parser.*

object ReduceFunction: TypedInvocableFunction {
    override fun resolve(invocation: FunctionInvoc): Type {
        val lambda = invocation.args[2] as Lambda
        lambda.parameters[0].type = invocation.args[1].type
        lambda.parameters[1].type = (invocation.args[0].type as Type.Sequence).innerType
        return lambda.body.type
    }

    override fun invoke(invocation: FunctionInvoc, args: List<Any?>): Any? {
        val sequence = args[0] as SequenceValue<Number>?
        val initial = args[1] as Number?
        val lambda = args[2] as InvocableLambda?
        if (lambda == null || initial == null || sequence == null) return null

        return invocation.type.accept(object: Type.Visitor<Number> {
            override fun visitInteger(): Number {
                return sequence.reduce(initial as Int) { state,n -> lambda(listOf(state, n)) as Int }
            }

            override fun visitReal(): Number {
                return sequence.reduce(initial.toDouble()) { state,n -> lambda(listOf(state, n)) as Double }
            }
        })
    }
}
