package com.fkistner.SouthQuay.parser

interface Function {
    fun resolve(invocation: FunctionInvoc): Type
    fun verify(argumentTypes: List<Type>): Boolean
}
