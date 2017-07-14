package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*
import com.fkistner.SouthQuay.parser.Function


object MapFunction : Function {
    override val type get() = Type.Sequence
}