package com.fkistner.SouthQuay.parser

/** Super class for all types expression can resolve to. */
sealed class Type {
    /** Error type. */
    object Error: Type() {
        override fun toString(): String = "Error"
    }

    /** Integer type. */
    object Integer: Type() {
        override fun toString(): String = "Integer"
    }

    /** Real type. */
    object Real: Type() {
        override fun toString(): String = "Real"
    }

    /**
     * Sequence type of [innerType].
     * @param innerType Type of sequence elements
     */
    data class Sequence(val innerType: Type): Type() {
        override fun toString(): String = "Sequence<$innerType>"
    }

    /**
     * Lambda type.
     */
    object Lambda: Type() {
        override fun toString(): String = "Lambda"
    }
}
