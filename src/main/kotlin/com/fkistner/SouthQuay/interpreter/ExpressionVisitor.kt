package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*


class ExpressionVisitor: ASTVisitor<Any> {
    override fun visit(integerLiteral: IntegerLiteral): Any? {
        return integerLiteral.value
    }

    override fun visit(realLiteral: RealLiteral): Any? {
        return realLiteral.value
    }

    private inline fun evaluate(binaryOperation: BinaryOperation, op: (Any, Any) -> Any?): Any? {
        val left  = binaryOperation.left.accept(this)
        val right = binaryOperation.right.accept(this)
        if (left == null || right == null) return null
        return op(left, right)
    }

    override fun visit(sum: Sum): Any? {
        return evaluate(sum) { left, right ->
            when (sum.type) {
                Type.Integer -> left as Int + right as Int
                Type.Real    -> (left as Number).toDouble() + (right as Number).toDouble()
                else -> throw IllegalStateException()
            }
        }
    }

    override fun visit(sub: Sub): Any? {
        return evaluate(sub) { left, right ->
            when (sub.type) {
                Type.Integer -> left as Int - right as Int
                Type.Real    -> (left as Number).toDouble() - (right as Number).toDouble()
                else -> throw IllegalStateException()
            }
        }
    }

    override fun visit(mul: Mul): Any? {
        return evaluate(mul) { left, right ->
            when (mul.type) {
                Type.Integer -> left as Int * right as Int
                Type.Real    -> (left as Number).toDouble() * (right as Number).toDouble()
                else -> throw IllegalStateException()
            }
        }
    }

    override fun visit(div: Div): Any? {
        return evaluate(div) { left, right ->
            when (div.type) {
                Type.Integer -> left as Int / right as Int
                Type.Real    -> (left as Number).toDouble() / (right as Number).toDouble()
                else -> throw IllegalStateException()
            }
        }
    }

    override fun visit(pow: Pow): Any? {
        return evaluate(pow) { left, right ->
            when (pow.type) {
                Type.Integer -> Math.pow((left as Int).toDouble(), (right as Int).toDouble()).toInt()
                Type.Real    -> Math.pow((left as Number).toDouble(), (right as Number).toDouble())
                else -> throw IllegalStateException()
            }
        }
    }
}