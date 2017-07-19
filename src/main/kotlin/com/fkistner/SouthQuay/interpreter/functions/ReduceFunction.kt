package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.interpreter.values.SequenceValue
import com.fkistner.SouthQuay.parser.*

/**
 * Implementation of the `reduce` function, that allows recursively combining values of a sequence using a lambda function.
 *
 * `reduce({1, 10}, 0, i -> i+i)`
 */
object ReduceFunction: TypedInvocableFunction {
    override val signature = FunctionSignature("reduce", listOf("sequence", "neutral", "lambda"))

    override fun resolve(invocation: FunctionInvoc): Type {
        (invocation.args[2] as? Lambda)?.let { (parameters, body) ->
            parameters[0].type = invocation.args[1].type
            parameters[1].type = (invocation.args[0].type as? Type.Sequence)?.innerType ?: Type.Error
            return body.type
        }
        return Type.Error
    }

    override fun verify(argumentTypes: List<Type>) = argumentTypes[0] is Type.Sequence
            && (argumentTypes[1] == Type.Integer || argumentTypes[1] == Type.Real)
            && argumentTypes[2] == Type.Lambda

    override fun invoke(invocation: FunctionInvoc, args: List<Any?>): Any? {
        val sequence = args[0] as SequenceValue<Number>?
        val neutral = args[1] as Number?
        val lambda = args[2] as InvocableLambda?
        if (lambda == null || neutral == null || sequence == null) return null

        return when(invocation.type) {
            Type.Integer -> sequence.reduce(neutral as Int)     { state,n -> lambda(listOf(state, n)) as Int }
            Type.Real    -> sequence.reduce(neutral.toDouble()) { state,n -> lambda(listOf(state, n)) as Double }
            else -> throw IllegalStateException()
        }
    }
}
