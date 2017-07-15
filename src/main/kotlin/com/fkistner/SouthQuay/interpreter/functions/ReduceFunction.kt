package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.interpreter.values.SequenceValue
import com.fkistner.SouthQuay.parser.Type

object ReduceFunction: TypedInvocableFunction {
    override val type get() = Type.Integer

    override fun invoke(args: List<Any?>): Any? {
        val sequence = args[0] as SequenceValue?
        val initial = args[1] as Int?
        val lambda = args[2] as InvocableFunction?
        if (lambda == null || initial == null || sequence == null) return null
        return sequence.stream().reduce(initial) { state,n -> lambda(listOf(state, n)) as Int }
    }
}
