package com.fkistner.SouthQuay.parser

interface Function {
    fun resolve(invocation: FunctionInvoc): Type
}
