package com.fkistner.SouthQuay.interpreter.values

import java.util.*
import java.util.stream.BaseStream


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
