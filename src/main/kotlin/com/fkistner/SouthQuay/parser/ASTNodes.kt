package com.fkistner.SouthQuay.parser

sealed class ASTNode {
    open val children: List<ASTNode> = emptyList()
    fun <T>accept(visitor: ASTVisitor<T>): T? {
        val returnValue = visit(visitor)
        endVisit(visitor)
        return returnValue
    }

    fun <T>acceptChildren(visitor: ASTVisitor<T>) {
        children.map { it.accept(visitor) }
    }

    protected abstract fun <T>   visit(visitor: ASTVisitor<T>): T?
    protected abstract fun <T>endVisit(visitor: ASTVisitor<T>)
}

data class Program(val statements: List<Statement> = listOf()): ASTNode() {
    override val children get() = statements
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

sealed class Statement : ASTNode()
data class PrintStatement(val stringLiteral: String): Statement() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class OutStatement(val expression: Expression): Statement() {
    override val children get() = listOf(expression)
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

data class VarDeclaration(val identifier: String): ASTNode() {
    var type: Type = Type.Error

    constructor(identifier: String, type: Type): this(identifier) {
        this.type = type
    }

    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class VarStatement(val declaration: VarDeclaration, val expression: Expression): Statement() {
    override val children get() = listOf(declaration, expression)
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

sealed class Expression : ASTNode() {
    abstract val type: Type
}

data class IntegerLiteral(val value: Int)   : Expression() {
    override val type get() = Type.Integer
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class RealLiteral   (val value: Double): Expression() {
    override val type get() = Type.Real
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class Sequence      (val from: Expression, val to: Expression): Expression() {
    override val type get() = Type.Sequence(Type.Integer)
    override val children get() = listOf(from, to)
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

sealed class BinaryOperation: Expression() {
    override val type get() = resolve(left, right)
    abstract val left: Expression
    abstract val right: Expression
    override val children get() = listOf(left, right)
}
fun resolve(left: Expression, right: Expression): Type {
    return when {
        left.type === Type.Integer && right.type === Type.Integer -> Type.Integer
        left.type === Type.Integer && right.type === Type.Real
        || left.type === Type.Real && right.type === Type.Integer
        || left.type === Type.Real && right.type === Type.Real -> Type.Real
        else -> Type.Error
    }
}

data class Sum(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class Sub(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class Mul(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class Div(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class Pow(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

data class FunctionInvoc(val identifier: String, val args: List<Expression>): Expression() {
    var target: Function? = null
        set(value) {
            field = value
            value?.let { type = it.resolve(this) }
        }

    constructor(identifier: String, args: List<Expression>, target: Function?): this(identifier, args) {
        this.target = target
    }

    override var type: Type = Type.Error
    override val children get() = args
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

data class Lambda(val parameters: List<VarDeclaration>, val body: Expression): Expression() {
    override val type get() = Type.Lambda
    override val children get() = parameters + listOf(body)
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
data class VariableRef(val identifier: String): Expression() {
    var declaration: VarDeclaration? = null

    constructor(identifier: String, declaration: VarDeclaration?): this(identifier) {
        this.declaration = declaration
    }

    override val type get() = declaration?.type ?: Type.Error
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
