package com.fkistner.SouthQuay.parser

sealed class Type {
    interface Visitor<T> {
        fun visitInteger(): T? = null
        fun visitReal(): T? = null
        fun visitSequence(innerType: Type): T? = null
        fun visitLambda(): T? = null
    }
    open fun <T>accept(visitor: Visitor<T>): T? = null
    object Error: Type() {
        override fun toString(): String = "Error"
    }
    object Integer: Type() {
        override fun <T>accept(visitor: Visitor<T>) = visitor.visitInteger()
        override fun toString(): String = "Integer"
    }
    object Real: Type() {
        override fun <T>accept(visitor: Visitor<T>) = visitor.visitReal()
        override fun toString(): String = "Real"
    }
    data class Sequence(val innerType: Type): Type() {
        override fun <T>accept(visitor: Visitor<T>) = visitor.visitSequence(innerType)
        override fun toString(): String = "Sequence<$innerType>"
    }
    object Lambda: Type() {
        override fun <T>accept(visitor: Visitor<T>) = visitor.visitLambda()
        override fun toString(): String = "Lambda"
    }
}
