package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.interpreter.values.last
import org.junit.*
import java.util.*
import java.util.stream.*


class StreamTests {
    @Test
    fun exactSizeKnownForIntRange() {
        val rangeClosed = IntStream.rangeClosed(1, 1_000_000_000)

        Assert.assertEquals(1_000_000_000, rangeClosed.spliterator().exactSizeIfKnown)
    }

    @Test
    fun exactSizeKnownAfterMap() {
        val rangeClosed = IntStream.rangeClosed(1, 1_000_000_000)
        val stringStream = rangeClosed.mapToObj(Int::toString)

        Assert.assertEquals(1_000_000_000, stringStream.spliterator().exactSizeIfKnown)
    }

    @Test
    fun exactSizeUNKNOWNAfterSkip() {
        val rangeClosed = IntStream.rangeClosed(1, 1_000_000_000)
        val stringStream = rangeClosed.skip(1_000_000_000 - 1)

        Assert.assertEquals(-1, stringStream.spliterator().exactSizeIfKnown)
    }

    @Test
    fun exactSizeKnownAfterConcatInt() {
        val rangeA = IntStream.rangeClosed(1, 1_000_000)
        val rangeB = IntStream.rangeClosed(1, 5_000_000)
        val concatStream = IntStream.concat(rangeA, rangeB)

        Assert.assertEquals(6_000_000, concatStream.spliterator().exactSizeIfKnown)
    }

    @Test
    fun exactSizeKnownAfterConcatString() {
        val rangeA = IntStream.rangeClosed(1, 1_000_000).mapToObj(Int::toString)
        val rangeB = IntStream.rangeClosed(1, 5_000_000).mapToObj(Int::toString)
        val concatStream = Stream.concat(rangeA, rangeB)

        Assert.assertEquals(6_000_000, concatStream.spliterator().exactSizeIfKnown)
    }

    @Test(expected = IllegalStateException::class)
    fun lastElementSlow() {
        val array = IntArray(1000001) { 0 }
        array[1000000] = 1

        val riggedStream = Arrays.stream(array).mapToObj {
            if (it == 1) "HELLO"
            else throw IllegalStateException()
        }

        Assert.assertEquals("HELLO", riggedStream.skip(1000000).findFirst().get())
    }

    @Test
    fun lastElementFast() {
        val array = IntArray(1000001) { 0 }
        array[1000000] = 1

        val riggedStream = Arrays.stream(array).mapToObj {
            if (it == 1) "HELLO"
            else throw IllegalStateException()
        }

        Assert.assertEquals("HELLO", riggedStream.last)
    }
}