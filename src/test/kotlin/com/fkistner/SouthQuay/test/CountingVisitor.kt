package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.parser.*


open class CountingVisitor : ASTVisitor {
    var visitCounter = 0

    override fun visit(variableRef: VariableRef): Boolean {
        visitCounter++
        return false
    }

    override fun visit(lambda: Lambda): Boolean {
        visitCounter++
        return false
    }

    override fun visit(functionInvoc: FunctionInvoc): Boolean {
        visitCounter++
        return false
    }

    override fun visit(sum: Sum): Boolean {
        visitCounter++
        return false
    }

    override fun visit(sub: Sub): Boolean {
        visitCounter++
        return false
    }

    override fun visit(mul: Mul): Boolean {
        visitCounter++
        return false
    }

    override fun visit(div: Div): Boolean {
        visitCounter++
        return false
    }

    override fun visit(pow: Pow): Boolean {
        visitCounter++
        return false
    }

    override fun visit(sequence: Sequence): Boolean {
        visitCounter++
        return false
    }

    override fun visit(realLiteral: RealLiteral): Boolean {
        visitCounter++
        return false
    }

    override fun visit(integerLiteral: IntegerLiteral): Boolean {
        visitCounter++
        return false
    }

    override fun visit(varStatement: VarStatement): Boolean {
        visitCounter++
        return false
    }

    override fun visit(outStatement: OutStatement): Boolean {
        visitCounter++
        return false
    }

    override fun visit(printStatement: PrintStatement): Boolean {
        visitCounter++
        return false
    }

    override fun visit(program: Program): Boolean {
        visitCounter++
        return false
    }
}