package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*


class ExecutionContext {
    val activeValues = mutableMapOf<VarDeclaration, Any?>()
}