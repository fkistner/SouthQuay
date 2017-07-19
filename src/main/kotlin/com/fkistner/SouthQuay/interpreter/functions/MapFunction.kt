package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.interpreter.values.SequenceValue
import com.fkistner.SouthQuay.parser.*

/**
 * Implementation of the `map` function, that allows applying a lambda function to the values of a sequence.
 *
 * `map({1, 10}, i -> i*i)`
 */
object MapFunction : TypedInvocableFunction {
    override val signature = FunctionSignature("map", listOf("sequence", "lambda"))

    override fun resolve(invocation: FunctionInvoc): Type {
        (invocation.args[1] as? Lambda)?.let { (parameters, body) ->
            parameters[0].type = (invocation.args[0].type as? Type.Sequence)?.innerType ?: Type.Error
            return Type.Sequence(body.type)
        }
        return Type.Error
    }

    override fun verify(argumentTypes: List<Type>) = argumentTypes[0] is Type.Sequence && argumentTypes[1] == Type.Lambda

    override fun invoke(invocation: FunctionInvoc, args: List<Any?>): Any? {
        val sequence = args[0] as SequenceValue<Number>?
        val lambda = args[1] as InvocableLambda?
        if (lambda == null || sequence == null) return null

        val type = (invocation.type as Type.Sequence).innerType
        return when(type) {
            Type.Integer -> {
                val mapper = { n: Number -> lambda(listOf(n)) as Int }
                sequence.map(mapper)
            }
            Type.Real -> {
                val mapper = { n: Number -> lambda(listOf(n)) as Double }
                sequence.map(mapper)
            }
            else -> throw IllegalStateException()
        }
    }
}
