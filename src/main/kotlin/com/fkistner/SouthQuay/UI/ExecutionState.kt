package com.fkistner.SouthQuay.UI

import com.fkistner.SouthQuay.parser.SQLangError

interface ExecutionState<out T> {
    fun run(): T
    fun abort(): T
    fun finish(): T
}
