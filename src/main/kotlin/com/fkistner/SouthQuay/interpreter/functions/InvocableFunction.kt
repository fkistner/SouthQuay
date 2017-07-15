package com.fkistner.SouthQuay.interpreter.functions

import com.fkistner.SouthQuay.parser.*
import com.fkistner.SouthQuay.parser.Function

interface InvocableLambda: (List<Any?>) -> Any?
interface InvocableFunction: (FunctionInvoc, List<Any?>) -> Any?
interface TypedInvocableFunction: InvocableFunction, Function
