package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.Type

object MapFunction : TypedInvocableFunction {
    override val type get() = Type.Sequence

    override fun invoke(args: List<Any?>): Any? {
        TODO("not implemented")
    }
}
