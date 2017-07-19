package com.fkistner.SouthQuay.interpreter.values

import java.util.*
import java.util.stream.BaseStream


/**
 * Efficiently gets the last value of a stream by splitting its underlying spliterator
 * until only one element (or close to) is left.
 *
 * The spliterator is required to be [Spliterator.ORDERED], [Spliterator.SIZED], and [Spliterator.SUBSIZED].
 * @param T Element type
 * @param S Stream type
 * @return Last element, if spliterator fulfills requirements, otherwise `null`
 */
val <T, S: BaseStream<T, S>> BaseStream<T, S>.last: T?
    get() {
        // Spliterator might not allow splitting, if not in parallel mode
        val suffix = parallel().spliterator()
        
        val requiredBits = Spliterator.ORDERED or Spliterator.SIZED or Spliterator.SUBSIZED
        if (suffix.characteristics() and requiredBits != requiredBits) return null

        val prefix = suffix.trySplit()?.let {
            var prefix = it
            while (prefix.exactSizeIfKnown > 0L) {
                prefix = suffix.trySplit() ?: break
            }
            prefix
        }

        var hasValue = false
        var element: T? = null

        suffix.forEachRemaining { elem ->
            hasValue = true
            element = elem
        }
        if (hasValue) return element

        prefix?.forEachRemaining { elem ->
            element = elem
        }
        return element
    }
