package com.fkistner.SouthQuay.parser

/** Super class for all types expression can resolve to. */
sealed class Type {
    /**
     * Interface for visitors of a type instance. Implementations may return and thereby pass back values.
     * @param T Type of returned values
     */
    interface Visitor<T> {
        fun visitInteger(): T? = null
        fun visitReal(): T? = null
        fun visitSequence(innerType: Type): T? = null
        fun visitLambda(): T? = null
    }

    /**
     * Accepts a type visitor, visits it, and returns provided value.
     *
     * @param visitor Type visitor
     * @param T Type of returned value
     * @return The value returned by [Visitor]
     */
    open fun <T>accept(visitor: Visitor<T>): T? = null

    /** Error type. */
    object Error: Type() {
        override fun toString(): String = "Error"
    }

    /** Integer type. */
    object Integer: Type() {
        override fun <T>accept(visitor: Visitor<T>) = visitor.visitInteger()
        override fun toString(): String = "Integer"
    }

    /** Real type. */
    object Real: Type() {
        override fun <T>accept(visitor: Visitor<T>) = visitor.visitReal()
        override fun toString(): String = "Real"
    }

    /**
     * Sequence type of [innerType].
     * @param innerType Type of sequence elements
     */
    data class Sequence(val innerType: Type): Type() {
        override fun <T>accept(visitor: Visitor<T>) = visitor.visitSequence(innerType)
        override fun toString(): String = "Sequence<$innerType>"
    }

    /**
     * Lambda type.
     */
    object Lambda: Type() {
        override fun <T>accept(visitor: Visitor<T>) = visitor.visitLambda()
        override fun toString(): String = "Lambda"
    }
}
