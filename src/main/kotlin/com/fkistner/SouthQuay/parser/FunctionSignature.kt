package com.fkistner.SouthQuay.parser

data class FunctionSignature(val identifier: String, val argumentCount: Int) {
    lateinit var argumentNames: List<String>

    constructor(identifier: String, argumentNames: List<String>): this(identifier, argumentNames.size) {
        this.argumentNames = argumentNames
    }
}
