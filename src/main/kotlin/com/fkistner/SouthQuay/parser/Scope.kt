package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.interpreter.*

class Scope(val errorContainer: MutableList<SQLangError>) {
    val variables = mutableMapOf<String, VarStatement>()
    val functions = mapOf(Pair("map", MapFunction), Pair("reduce", ReduceFunction))
}
