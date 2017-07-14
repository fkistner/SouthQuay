package com.fkistner.SouthQuay.parser


open class CountingVisitor : ASTVisitor {
    var visitCounter = 0
    var endVisitCounter = 0

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

    override fun visit(varDeclaration: VarDeclaration): Boolean {
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

    override fun endVisit(variableRef: VariableRef) {
        endVisitCounter++
    }

    override fun endVisit(lambda: Lambda) {
        endVisitCounter++
    }

    override fun endVisit(functionInvoc: FunctionInvoc) {
        endVisitCounter++
    }

    override fun endVisit(sum: Sum) {
        endVisitCounter++
    }

    override fun endVisit(sub: Sub) {
        endVisitCounter++
    }

    override fun endVisit(mul: Mul) {
        endVisitCounter++
    }

    override fun endVisit(div: Div) {
        endVisitCounter++
    }

    override fun endVisit(pow: Pow) {
        endVisitCounter++
    }

    override fun endVisit(sequence: Sequence) {
        endVisitCounter++
    }

    override fun endVisit(realLiteral: RealLiteral) {
        endVisitCounter++
    }

    override fun endVisit(integerLiteral: IntegerLiteral) {
        endVisitCounter++
    }

    override fun endVisit(varDeclaration: VarDeclaration) {
        endVisitCounter++
    }

    override fun endVisit(varStatement: VarStatement) {
        endVisitCounter++
    }

    override fun endVisit(outStatement: OutStatement) {
        endVisitCounter++
    }

    override fun endVisit(printStatement: PrintStatement) {
        endVisitCounter++
    }

    override fun endVisit(program: Program) {
        endVisitCounter++
    }
}