package com.fkistner.SouthQuay.parser


/**
 * Checks for type errors and records them in the error container of the [scope] by visiting the abstract syntax tree.
 * @param scope Current visibility scope
 */
class ASTVerifier(val scope: Scope): ASTVisitor<Unit> {
    override fun visit(lambda: Lambda) = lambda.verify()
    override fun visit(functionInvoc: FunctionInvoc) {
        functionInvoc.acceptChildren(this)
        functionInvoc.target?.let { target ->
            val argumentTypes = functionInvoc.args.map(Expression::type)
            if (!target.verify(argumentTypes))
                scope.errorContainer.add(TypeError("Incompatible arguments ${functionInvoc.identifier}(" +
                        "${argumentTypes.joinToString()}) for function", functionInvoc))
        }
    }

    /** Verifies the types of this binary operation. */
    fun BinaryOperation.checkBinaryOperation() {
        acceptChildren(this@ASTVerifier)
        if (left.type == Type.Error || right.type == Type.Error || type != Type.Error) return
        scope.errorContainer.add(TypeError("Incompatible arguments ${left.type} and ${right.type} for operator",
                this))
    }

    override fun visit(sum: Sum) = sum.checkBinaryOperation()
    override fun visit(sub: Sub) = sub.checkBinaryOperation()
    override fun visit(mul: Mul) = mul.checkBinaryOperation()
    override fun visit(div: Div) = div.checkBinaryOperation()
    override fun visit(pow: Pow) = pow.checkBinaryOperation()

    override fun visit(sequence: Sequence) {
        sequence.acceptChildren(this)
        with(sequence) {
            when (from.type) {
                Type.Error, Type.Integer -> {}
                else -> scope.errorContainer.add(TypeError("Illegal sequence start, expected Integer, " +
                        "found $from.type", from))
            }
            when (to.type) {
                Type.Error, Type.Integer -> {}
                else -> scope.errorContainer.add(TypeError("Illegal sequence end, expected Integer, " +
                        "found $to.type", to))
            }
        }
    }

    override fun visit(varStatement: VarStatement) = varStatement.acceptChildren(this)
    override fun visit(outStatement: OutStatement) = outStatement.acceptChildren(this)
    override fun visit(program: Program) = program.acceptChildren(this)
}
