package com.fkistner.SouthQuay.parser


open class CountingVisitor : ASTVisitor<Unit> {
    var visitCounter = 0
    var endVisitCounter = 0

    override fun visit(variableRef: VariableRef) {
        visitCounter++
    }

    override fun visit(lambda: Lambda) {
        visitCounter++
    }

    override fun visit(functionInvoc: FunctionInvoc) {
        visitCounter++
    }

    override fun visit(sum: Sum) {
        visitCounter++
    }

    override fun visit(sub: Sub) {
        visitCounter++
    }

    override fun visit(mul: Mul) {
        visitCounter++
    }

    override fun visit(div: Div) {
        visitCounter++
    }

    override fun visit(pow: Pow) {
        visitCounter++
    }

    override fun visit(sequence: Sequence) {
        visitCounter++
    }

    override fun visit(realLiteral: RealLiteral) {
        visitCounter++
    }

    override fun visit(integerLiteral: IntegerLiteral) {
        visitCounter++
    }

    override fun visit(varDeclaration: VarDeclaration) {
        visitCounter++
    }

    override fun visit(varStatement: VarStatement) {
        visitCounter++
    }

    override fun visit(outStatement: OutStatement) {
        visitCounter++
    }

    override fun visit(printStatement: PrintStatement) {
        visitCounter++
    }

    override fun visit(program: Program) {
        visitCounter++
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