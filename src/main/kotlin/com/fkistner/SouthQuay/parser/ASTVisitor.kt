package com.fkistner.SouthQuay.parser

interface ASTVisitor {
    fun visit(variableRef: VariableRef) = true
    fun visit(lambda: Lambda) = true
    fun visit(functionInvoc: FunctionInvoc) = true
    fun visit(sum: Sum) = true
    fun visit(sub: Sub) = true
    fun visit(mul: Mul) = true
    fun visit(div: Div) = true
    fun visit(pow: Pow) = true
    fun visit(sequence: Sequence) = true
    fun visit(realLiteral: RealLiteral) = true
    fun visit(integerLiteral: IntegerLiteral) = true
    fun visit(varDeclaration: VarDeclaration) = true
    fun visit(varStatement: VarStatement) = true
    fun visit(outStatement: OutStatement) = true
    fun visit(printStatement: PrintStatement) = true
    fun visit(program: Program) = true
    fun endVisit(variableRef: VariableRef) = Unit
    fun endVisit(lambda: Lambda) = Unit
    fun endVisit(functionInvoc: FunctionInvoc) = Unit
    fun endVisit(sum: Sum) = Unit
    fun endVisit(sub: Sub) = Unit
    fun endVisit(mul: Mul) = Unit
    fun endVisit(div: Div) = Unit
    fun endVisit(pow: Pow) = Unit
    fun endVisit(sequence: Sequence) = Unit
    fun endVisit(realLiteral: RealLiteral) = Unit
    fun endVisit(integerLiteral: IntegerLiteral) = Unit
    fun endVisit(varDeclaration: VarDeclaration) = Unit
    fun endVisit(varStatement: VarStatement) = Unit
    fun endVisit(outStatement: OutStatement) = Unit
    fun endVisit(printStatement: PrintStatement) = Unit
    fun endVisit(program: Program) = Unit
}
