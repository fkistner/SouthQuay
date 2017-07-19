package com.fkistner.SouthQuay.parser


/** Abstract syntax tree node. */
sealed class ASTNode {
    /** List of child AST nodes, by default empty. */
    open val children: List<ASTNode> get() = emptyList()
    /** Source span node was created from. */
    lateinit var span: Span

    /**
     * Accepts an AST visitor, initiates visitation, and returns provided value.
     *
     * Children are not visited by default. Visitors may call [acceptChildren] to descend.
     * @param visitor AST visitor
     * @param T Type of returned value
     * @return The value returned by [ASTVisitor.visit]
     * @see acceptChildren
     */
    fun <T>accept(visitor: ASTVisitor<T>): T? {
        val returnValue = visit(visitor)
        endVisit(visitor)
        return returnValue
    }

    /**
     * Accepts an AST visitor for all children.
     *
     * This node is not visited.
     * @param visitor AST visitor
     * @see accept
     */
    fun <T>acceptChildren(visitor: ASTVisitor<T>) {
        children.map { it.accept(visitor) }
    }

    /**
     * Dispatches visitation with concrete type, and returns provided value.
     * @param visitor AST visitor
     * @param T Type of returned value
     * @return The value returned by [ASTVisitor.visit]
     */
    protected abstract fun <T>   visit(visitor: ASTVisitor<T>): T?

    /**
     * Dispatches end of visit notification.
     * @param visitor AST visitor
     */
    protected abstract fun <T>endVisit(visitor: ASTVisitor<T>)
}

/**
 * Root AST node presenting an entire program.
 * @param statements Statements the program consists of
 */
data class Program(val statements: List<Statement> = listOf()): ASTNode() {
    /** Variable and function scope that determines visibility of identifiers. */
    lateinit var scope: Scope

    /**
     * Root AST node presenting an entire program.
     *
     * This secondary constructor initializes the [scope].
     * @param statements Statements the program consists of
     * @param scope Variable and function scope that determines visibility of identifiers
     */
    constructor(statements: List<Statement>, scope: Scope): this(statements) {
        this.scope = scope
    }

    override val children get() = statements
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/** Super class of all statement nodes. */
sealed class Statement : ASTNode()

/**
 * Print statement.
 *
 * `print "stringLiteral"`
 * @param stringLiteral String literal to be output
 */
data class PrintStatement(val stringLiteral: String): Statement() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Out statement.
 *
 * `out `*`<expression>`*
 * @param expression Expression to be output
 */
data class OutStatement(val expression: Expression): Statement() {
    override val children get() = listOf(expression)
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}


/**
 * Variable/parameter declaration.
 *
 * `var identifier` or `identifier ->`
 * @param identifier Identifier declared
 */
data class VarDeclaration(val identifier: String): ASTNode() {
    /** Type of the variable/parameter */
    var type: Type = Type.Error

    /**
     * Variable/parameter declaration.
     *
     * This secondary constructor initializes the [type].
     * @param identifier Identifier declared
     * @param type Type of the variable/parameter
     */
    constructor(identifier: String, type: Type): this(identifier) {
        this.type = type
    }

    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Var statement.
 *
 * `var identifier = `*`<expression>`*
 * @param stringLiteral String literal
 */
data class VarStatement(val declaration: VarDeclaration, val expression: Expression): Statement() {
    override val children get() = listOf(declaration, expression)
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Super class of all expression nodes.
 */
sealed class Expression : ASTNode() {
    /** Type the expression evaluates to. */
    abstract val type: Type
}

/**
 * Integer literal expression.
 *
 * `42`
 * @param value Integer value
 */
data class IntegerLiteral(val value: Int)   : Expression() {
    override val type get() = Type.Integer
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Real literal expression.
 *
 * `42.123`
 * @param value Double value
 */
data class RealLiteral   (val value: Double): Expression() {
    override val type get() = Type.Real
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Sequence literal expression representing an inclusive number sequence.
 *
 * `{1, 10}`
 * @param from Lower bound
 * @param to Upper bound (inclusive)
 */
data class Sequence      (val from: Expression, val to: Expression): Expression() {
    override val type get() = Type.Sequence(Type.Integer)
    override val children get() = listOf(from, to)
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/** Super class of all binary operation expressions. */
sealed class BinaryOperation: Expression() {
    override val type get() = resolve(left, right)
    /** Left operand */
    abstract val left: Expression
    /** Right operand */
    abstract val right: Expression
    override val children get() = listOf(left, right)

    /**
     * Resolves the type the binary operation evaluates to.
     * If one of the operands is of [Type.Real], [Type.Integer] to [Type.Real] promotion will be applied.
     * @param left Left operand
     * @param right Right operand
     */
    fun resolve(left: Expression, right: Expression): Type {
        return when {
            left.type === Type.Integer && right.type === Type.Integer -> Type.Integer
            left.type === Type.Integer && right.type === Type.Real
                    || left.type === Type.Real && right.type === Type.Integer
                    || left.type === Type.Real && right.type === Type.Real -> Type.Real
            else -> Type.Error
        }
    }
}

/**
 * Sum binary operation expression.
 *
 * `4 + 7`
 */
data class Sum(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Subtract binary operation expression.
 *
 * `42 - 9`
 */
data class Sub(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Multiply binary operation expression.
 *
 * `3 * 12`
 */
data class Mul(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Divide binary operation expression.
 *
 * `11 / 2`
 */
data class Div(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Power binary operation expression.
 *
 * `17^2`
 */
data class Pow(override val left: Expression, override val right: Expression): BinaryOperation() {
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Function invocation expression.
 *
 * `map(…, …)`
 * @param identifier Name of the function
 * @param args Arguments of the function
 */
data class FunctionInvoc(val identifier: String, val args: List<Expression>): Expression() {
    /** Resolved function this invocation targets. */
    var target: Function? = null
        set(value) {
            field = value
            value?.let { type = it.resolve(this) }
        }

    /**
     * Function invocation expression.
     *
     * This secondary constructor initializes the [target].
     * @param identifier Name of the function
     * @param args Arguments of the function
     * @param target Resolved function this invocation targets
     */
    constructor(identifier: String, args: List<Expression>, target: Function?): this(identifier, args) {
        this.target = target
    }

    override var type: Type = Type.Error
    override val children get() = args
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Lambda function expression.
 *
 * `x y -> x*y`
 * @param parameters List of declared parameters
 * @param body Body expression that is evaluated as function
 */
data class Lambda(val parameters: List<VarDeclaration>, val body: Expression): Expression() {
    /** Variable and function scope that determines visibility of identifiers. */
    lateinit var scope: Scope

    /**
     * Lambda function expression.
     *
     * This secondary constructor initializes the [scope].
     * @param parameters List of declared parameters
     * @param body Body expression that is evaluated as function
     * @param scope Variable and function scope that determines visibility of identifiers
     */
    constructor(parameters: List<VarDeclaration>, body: Expression, scope: Scope): this(parameters, body) {
        this.scope = scope
    }

    override val type get() = Type.Lambda
    override val children get() = parameters + listOf(body)
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}

/**
 * Variable reference expression.
 *
 * `x`
 * @param identifier Identifier of the variable
 */
data class VariableRef(val identifier: String): Expression() {
    /** Variable declaration this reference refers to. */
    var declaration: VarDeclaration? = null

    /**
     * Variable reference expression.
     *
     * This secondary constructor initializes the [declaration].
     * @param identifier Identifier of the variable
     * @param declaration Variable declaration this reference refers to
     */
    constructor(identifier: String, declaration: VarDeclaration?): this(identifier) {
        this.declaration = declaration
    }

    override val type get() = declaration?.type ?: Type.Error
    override fun <T>   visit(visitor: ASTVisitor<T>) = visitor.visit(this)
    override fun <T>endVisit(visitor: ASTVisitor<T>) = visitor.endVisit(this)
}
