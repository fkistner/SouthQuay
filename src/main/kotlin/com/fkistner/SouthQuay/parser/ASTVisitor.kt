package com.fkistner.SouthQuay.parser

interface ASTVisitor<out T> {
    fun visit(variableRef: VariableRef): T? = null
    fun visit(lambda: Lambda): T? = null
    fun visit(functionInvoc: FunctionInvoc): T? = null
    fun visit(sum: Sum): T? = null
    fun visit(sub: Sub): T? = null
    fun visit(mul: Mul): T? = null
    fun visit(div: Div): T? = null
    fun visit(pow: Pow): T? = null
    fun visit(sequence: Sequence): T? = null
    fun visit(realLiteral: RealLiteral): T? = null
    fun visit(integerLiteral: IntegerLiteral): T? = null
    fun visit(varDeclaration: VarDeclaration): T? = null
    fun visit(varStatement: VarStatement): T? = null
    fun visit(outStatement: OutStatement): T? = null
    fun visit(printStatement: PrintStatement): T? = null
    fun visit(program: Program): T? = null
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
