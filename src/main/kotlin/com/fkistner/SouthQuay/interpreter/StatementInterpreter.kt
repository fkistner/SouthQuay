package com.fkistner.SouthQuay.interpreter

import com.fkistner.SouthQuay.parser.*

/**
 * Evaluates program statements by visiting the abstract syntax tree.
 * @param executionParticipant Observer of the interpretation
 */
class StatementInterpreter(val executionParticipant: ExecutionParticipant): ASTVisitor<Unit>, ContextualInterpreter {
    override var context = ExecutionContext()

    override fun visit(program: Program) = program.acceptChildren(this)

    override fun visit(printStatement: PrintStatement) = trackError(printStatement) {
        executionParticipant.statementExecuting(printStatement)
        executionParticipant.output(printStatement, printStatement.stringLiteral)
    }

    override fun visit(outStatement: OutStatement) = trackError(outStatement) {
        executionParticipant.statementExecuting(outStatement)
        val expressionVisitor = ExpressionInterpreter(context)
        val result = outStatement.expression.accept(expressionVisitor)
        executionParticipant.output(outStatement, result.toString())
    }

    override fun visit(varStatement: VarStatement) = trackError(varStatement) {
        executionParticipant.statementExecuting(varStatement)
        val expressionVisitor = ExpressionInterpreter(context)
        val result = varStatement.expression.accept(expressionVisitor)
        context.activeValues[varStatement.declaration] = result
        executionParticipant.newValue(varStatement.declaration, result)
    }

    /**
     * Interprets the given program and optionally stores encountered errors.
     * @param program Program AST to interpret
     * @param errorContainer Store for encountered errors
     * @throws Throwable Rethrows encountered errors, if [errorContainer] is `null`
     */
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