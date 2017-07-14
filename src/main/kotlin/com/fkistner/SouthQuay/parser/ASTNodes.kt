package com.fkistner.SouthQuay.parser

sealed class ASTNode {
    open val children: List<ASTNode> = emptyList()
    fun accept(visitor: ASTVisitor) {
        if (visit(visitor)) children.map { it.accept(visitor) }
        endVisit(visitor)
    }
    protected abstract fun    visit(visitor: ASTVisitor): Boolean
    protected abstract fun endVisit(visitor: ASTVisitor)
}

data class Program(val statements: List<Statement> = listOf()): ASTNode() {
    override val children get() = statements
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

sealed class Statement : ASTNode()
data class PrintStatement(val stringLiteral: String) : Statement() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class OutStatement  (val expression: Expression): Statement() {
    override val children get() = listOf(expression)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class VarStatement  (val identifier: String, val expression: Expression): Statement() {
    override val children get() = listOf(expression)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

sealed class Expression : ASTNode()
data class IntegerLiteral(val value: Int)   : Expression() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class RealLiteral   (val value: Double): Expression() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Sequence      (val from: Expression, val to: Expression): Expression() {
    override val children get() = listOf(from, to)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

sealed class BinaryOperation: Expression() {
    abstract val left: Expression
    abstract val right: Expression
    override val children get() = listOf(left, right)
}

data class Sum(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Sub(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Mul(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Div(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Pow(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

data class FunctionInvoc(val identifier: String, val args: List<Expression>): Expression(){
    override val children get() = args
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Lambda(val parameters: List<String>, val body: Expression): Expression(){
    override val children get() = listOf(body)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class VariableRef(val identifier: String): Expression() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
