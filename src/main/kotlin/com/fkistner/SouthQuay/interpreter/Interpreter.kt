package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*

class Interpreter(val executionParticipant: InterpreterParticipant): ASTVisitor<Unit>, ContextualInterpreter {
    override var context = ExecutionContext()

    override fun visit(program: Program) = program.acceptChildren(this)

    override fun visit(printStatement: PrintStatement) = trackError(printStatement) {
        executionParticipant.statementExecuting(printStatement)
        executionParticipant.output(printStatement, printStatement.stringLiteral)
    }

    override fun visit(outStatement: OutStatement) = trackError(outStatement) {
        executionParticipant.statementExecuting(outStatement)
        val expressionVisitor = ExpressionVisitor(context)
        val result = outStatement.expression.accept(expressionVisitor)
        executionParticipant.output(outStatement, result.toString())
    }

    override fun visit(varStatement: VarStatement) = trackError(varStatement) {
        executionParticipant.statementExecuting(varStatement)
        val expressionVisitor = ExpressionVisitor(context)
        val result = varStatement.expression.accept(expressionVisitor)
        context.activeValues[varStatement.declaration] = result
        executionParticipant.newValue(varStatement.declaration, result.toString())
    }

    fun execute(program: Program, errorContainer: MutableList<SQLangError>? = null) {
        try {
            program.accept(this)
        } catch (t: Throwable) {
            val runtimeError = context.runtimeError
            if (errorContainer == null || runtimeError == null) throw t
            errorContainer.add(runtimeError)
        }
    }
}