package com.fkistner.SouthQuay.parser

/**
 * Interface for visitors of the abstract syntax tree. Implementations may return and thereby pass back values.
 * @param T Type of returned values
 */
interface ASTVisitor<out T> {
    /** Visits VariableRef expression. */
    fun visit(variableRef: VariableRef): T? = null
    /** Visits Lambda expression. */
    fun visit(lambda: Lambda): T? = null
    /** Visits FunctionInvoc expression. */
    fun visit(functionInvoc: FunctionInvoc): T? = null
    /** Visits Sum expression. */
    fun visit(sum: Sum): T? = null
    /** Visits Sub expression. */
    fun visit(sub: Sub): T? = null
    /** Visits Mul expression. */
    fun visit(mul: Mul): T? = null
    /** Visits Div expression. */
    fun visit(div: Div): T? = null
    /** Visits Pow expression. */
    fun visit(pow: Pow): T? = null
    /** Visits Sequence expression. */
    fun visit(sequence: Sequence): T? = null
    /** Visits RealLiteral expression. */
    fun visit(realLiteral: RealLiteral): T? = null
    /** Visits IntegerLiteral expression. */
    fun visit(integerLiteral: IntegerLiteral): T? = null
    /** Visits VarDeclaration. */
    fun visit(varDeclaration: VarDeclaration): T? = null
    /** Visits VarStatement. */
    fun visit(varStatement: VarStatement): T? = null
    /** Visits OutStatement. */
    fun visit(outStatement: OutStatement): T? = null
    /** Visits PrintStatement. */
    fun visit(printStatement: PrintStatement): T? = null
    /** Visits Program expression. */
    fun visit(program: Program): T? = null
    /** Notifies of end of VariableRef expression visit. */
    fun endVisit(variableRef: VariableRef) = Unit
    /** Notifies of end of Lambda expression visit. */
    fun endVisit(lambda: Lambda) = Unit
    /** Notifies of end of FunctionInvoc expression visit. */
    fun endVisit(functionInvoc: FunctionInvoc) = Unit
    /** Notifies of end of Sum expression visit. */
    fun endVisit(sum: Sum) = Unit
    /** Notifies of end of Sub expression visit. */
    fun endVisit(sub: Sub) = Unit
    /** Notifies of end of Mul expression visit. */
    fun endVisit(mul: Mul) = Unit
    /** Notifies of end of Div expression visit. */
    fun endVisit(div: Div) = Unit
    /** Notifies of end of Pow expression visit. */
    fun endVisit(pow: Pow) = Unit
    /** Notifies of end of Sequence expression visit. */
    fun endVisit(sequence: Sequence) = Unit
    /** Notifies of end of RealLiteral expression visit. */
    fun endVisit(realLiteral: RealLiteral) = Unit
    /** Notifies of end of IntegerLiteral expression visit. */
    fun endVisit(integerLiteral: IntegerLiteral) = Unit
    /** Notifies of end of VarDeclaration visit. */
    fun endVisit(varDeclaration: VarDeclaration) = Unit
    /** Notifies of end of VarStatement visit. */
    fun endVisit(varStatement: VarStatement) = Unit
    /** Notifies of end of OutStatement visit. */
    fun endVisit(outStatement: OutStatement) = Unit
    /** Notifies of end of PrintStatement visit. */
    fun endVisit(printStatement: PrintStatement) = Unit
    /** Notifies of end of Program visit. */
    fun endVisit(program: Program) = Unit
}
