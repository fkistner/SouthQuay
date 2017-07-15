package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.Function

typealias InvocableFunction = (List<Any?>) -> Any?
interface TypedInvocableFunction: InvocableFunction, Function
