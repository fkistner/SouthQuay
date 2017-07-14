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
    var scope: Scope? = null

    constructor(statements: List<Statement>, scope: Scope): this(statements) {
        this.scope = scope
    }

    override val children get() = statements
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

sealed class Statement : ASTNode()
data class PrintStatement(val stringLiteral: String): Statement() {
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class OutStatement(val expression: Expression): Statement() {
    override val children get() = listOf(expression)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

data class VarDeclaration(val identifier: String): ASTNode() {
    var type: Type = Type.Error

    constructor(identifier: String, type: Type): this(identifier) {
        this.type = type
    }

    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class VarStatement(val declaration: VarDeclaration, val expression: Expression): Statement() {
    override val children get() = listOf(declaration, expression)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

sealed class Type {
    object Error: Type() {
        override fun toString(): String = "Error"
    }
    object Integer: Type() {
        override fun toString(): String = "Integer"
    }
    object Real: Type() {
        override fun toString(): String = "Real"
    }
    object Sequence: Type() {
        override fun toString(): String = "Sequence"
    }
    data class Lambda(val type: Type): Type() {
        override fun toString(): String = "Lambda<$type>"
    }
}
sealed class Expression : ASTNode() {
    abstract val type: Type
}

data class IntegerLiteral(val value: Int)   : Expression() {
    override val type get() = Type.Integer
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class RealLiteral   (val value: Double): Expression() {
    override val type get() = Type.Real
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class Sequence      (val from: Expression, val to: Expression): Expression() {
    override val type get() = Type.Sequence
    override val children get() = listOf(from, to)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
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

interface Function {
    val type: Type
}
data class FunctionSignature(val identifier: String, val argumentTypes: List<Type>)

data class FunctionInvoc(val identifier: String, val args: List<Expression>): Expression() {
    var target: Function? = null

    constructor(identifier: String, args: List<Expression>, target: Function?): this(identifier, args) {
        this.target = target
    }

    override val type get() = target?.type ?: Type.Error
    override val children get() = args
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}

data class Lambda(val parameters: List<VarDeclaration>, val body: Expression): Expression() {
    var scope: Scope? = null

    constructor(parameters: List<VarDeclaration>, body: Expression, scope: Scope): this(parameters, body) {
        this.scope = scope
    }

    override val type get() = Type.Lambda(body.type)
    override val children get() = parameters + listOf(body)
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
data class VariableRef(val identifier: String): Expression() {
    var declaration: VarDeclaration? = null

    constructor(identifier: String, declaration: VarDeclaration?): this(identifier) {
        this.declaration = declaration
    }

    override val type get() = declaration?.type ?: Type.Error
    override fun    visit(visitor: ASTVisitor) = visitor.visit(this)
    override fun endVisit(visitor: ASTVisitor) = visitor.endVisit(this)
}
