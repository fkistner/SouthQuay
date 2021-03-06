package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.interpreter.functions.*
import com.fkistner.SouthQuay.interpreter.values.*
import com.fkistner.SouthQuay.parser.*
import java.util.stream.IntStream

/**
 * Evaluates expressions by visiting the abstract syntax tree.
 * @param context Execution context tracking active values and error source
 */
class ExpressionInterpreter(override val context: ExecutionContext): ASTVisitor<Any>, ContextualInterpreter {
    override fun visit(integerLiteral: IntegerLiteral) = integerLiteral.value
    override fun visit(realLiteral: RealLiteral) = realLiteral.value

    /**
     * Evaluates binary operation by applying function.
     * @param binaryOperation Binary operation node
     * @param op Function that applies operation
     * @return Result of the computation
     */
    private inline fun evaluate(binaryOperation: BinaryOperation, op: (Any, Any) -> Number?): Number? = trackError(binaryOperation) {
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

    override fun visit(sequence: Sequence): SequenceValue<Number>? = trackError(sequence) {
        val from = sequence.from.accept(this)
        val to = sequence.to.accept(this)
        if (from == null || to == null) return null
        val fromInt = from as Int; val toInt = to as Int
        return IntSequenceValue { IntStream.rangeClosed(fromInt, toInt) }
    }

    override fun visit(variableRef: VariableRef) = trackError(variableRef) {
        variableRef.declaration?.let { context.activeValues[it] }
    }

    override fun visit(functionInvoc: FunctionInvoc): Any? = trackError(functionInvoc) {
        val args = functionInvoc.args.map { it.accept(this) }
        return (functionInvoc.target as? InvocableFunction)?.invoke(functionInvoc, args)
    }

    override fun visit(lambda: Lambda) = object: InvocableLambda {
        override fun invoke(args: List<Any?>): Any? {
            val innerContext = ExecutionContext()
            for ((idx, value) in args.withIndex()) {
                innerContext.activeValues[lambda.parameters[idx]] = value
            }
            val visitor = ExpressionInterpreter(innerContext)
            try {
                return lambda.body.accept(visitor)
            } finally {
                if (context.runtimeError == null)
                    context.runtimeError = innerContext.runtimeError
            }
        }
    }
}