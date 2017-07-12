package com.fkistner.SouthQuay.test

/** Simplified tree of parsed rules and tokens */
internal sealed class TestTree

/** Rule Node */
internal data class N(val context: String, var children: List<TestTree> = listOf()): TestTree()

/** Leaf Node */
internal data class L(val text: String): TestTree()

/** Error Node */
internal data class Error(val text: String): TestTree()

/** EOF Node */
internal object EOF : TestTree()
