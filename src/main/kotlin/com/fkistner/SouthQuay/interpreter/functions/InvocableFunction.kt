package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.Function

interface InvocableFunction: (List<Any?>) -> Any?
interface TypedInvocableFunction: InvocableFunction, Function
