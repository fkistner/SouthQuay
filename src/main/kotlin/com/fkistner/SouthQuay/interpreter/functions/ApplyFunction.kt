package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.*

object ApplyFunction : TypedInvocableFunction {
    override val type get() = Type.Integer

    override fun invoke(args: List<Any?>): Any? {
        val lambda = args[1] as InvocableFunction?
        return args[0]?.let { lambda?.invoke(listOf(it)) }
    }
}
