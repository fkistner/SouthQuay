package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.interpreter.values.SequenceValue
import com.fkistner.SouthQuay.parser.Type

object MapFunction : TypedInvocableFunction {
    override val type get() = Type.Sequence(Type.Integer)

    override fun invoke(args: List<Any?>): Any? {
        val sequence = args[0] as SequenceValue?
        val lambda = args[1] as InvocableFunction?
        if (lambda == null || sequence == null) return null
        return SequenceValue { sequence.stream().map { lambda(listOf(it)) as Int } }
    }
}
