package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.Type

object ReduceFunction: TypedInvocableFunction {
    override val type get() = Type.Integer

    override fun invoke(args: List<Any?>): Any? {
        TODO("not implemented")
    }
}
