package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.interpreter.functions.*
import com.fkistner.SouthQuay.parser.*


class ExpressionVisitor(val context: ExecutionContext): ASTVisitor<Any> {
    override fun visit(integerLiteral: IntegerLiteral) = integerLiteral.value
    override fun visit(realLiteral: RealLiteral) = realLiteral.value

    private inline fun evaluate(binaryOperation: BinaryOperation, op: (Any, Any) -> Number?): Number? {
        val left  = binaryOperation.left.accept(this)
        val right = binaryOperation.right.accept(this)
        if (left == null || right == null) return null
        return op(left, right)
    }

    override fun visit(sum: Sum) = evaluate(sum) { left, right ->
        when (sum.type) {
            Type.Integer -> left as Int + right as Int
            Type.Real    -> (left as Number).toDouble() + (right as Number).toDouble()
            else -> throw IllegalStateException()
        }
    }

    override fun visit(sub: Sub) = evaluate(sub) { left, right ->
        when (sub.type) {
            Type.Integer -> left as Int - right as Int
            Type.Real    -> (left as Number).toDouble() - (right as Number).toDouble()
            else -> throw IllegalStateException()
        }
    }

    override fun visit(mul: Mul) = evaluate(mul) { left, right ->
        when (mul.type) {
            Type.Integer -> left as Int * right as Int
            Type.Real    -> (left as Number).toDouble() * (right as Number).toDouble()
            else -> throw IllegalStateException()
        }
    }

    override fun visit(div: Div) = evaluate(div) { left, right ->
        when (div.type) {
            Type.Integer -> left as Int / right as Int
            Type.Real    -> (left as Number).toDouble() / (right as Number).toDouble()
            else -> throw IllegalStateException()
        }
    }

    override fun visit(pow: Pow) = evaluate(pow) { left, right ->
        when (pow.type) {
            Type.Integer -> Math.pow((left as Int).toDouble(), (right as Int).toDouble()).toInt()
            Type.Real    -> Math.pow((left as Number).toDouble(), (right as Number).toDouble())
            else -> throw IllegalStateException()
        }
    }

    override fun visit(sequence: Sequence): IntRange? {
        val from = sequence.from.accept(this)
        val to = sequence.to.accept(this)
        if (from == null || to == null) return null
        return from as Int..to as Int
    }

    override fun visit(variableRef: VariableRef) = variableRef.declaration?.let { context.activeValues[it] }

    override fun visit(functionInvoc: FunctionInvoc): Any? {
        val args = functionInvoc.args.map { it.accept(this) }
        return (functionInvoc.target as? InvocableFunction)?.invoke(args)
    }

    override fun visit(lambda: Lambda) = object: InvocableFunction {
        override fun invoke(args: List<Any?>): Any? {
            val innerContext = ExecutionContext()
            for ((idx, value) in args.withIndex()) {
                innerContext.activeValues[lambda.parameters[idx]] = value
            }
            val visitor = ExpressionVisitor(innerContext)
            return lambda.body.accept(visitor)
        }
    }
}