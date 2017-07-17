package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*

class StatementASTBuilder(val scope: Scope): SQLangBaseVisitor<Statement>() {

    override fun visitPrint(ctx: SQLangParser.PrintContext) = PrintStatement(ctx.string.text.trim('"')).also { it.span = ctx.toSpan() }
    override fun visitOut  (ctx: SQLangParser.OutContext)   = OutStatement(ctx.expr.toAST(scope)).also { it.span = ctx.toSpan() }

    override fun visitVar(ctx: SQLangParser.VarContext): VarStatement {
        val identifier = ctx.ident.text
        val expression = ctx.expr.toAST(scope)
        val varDeclaration = VarDeclaration(identifier, expression.type).also { it.span = ctx.ident.toSpan() }
        val varStatement = VarStatement(varDeclaration, expression)
        if (scope.variables.containsKey(identifier)) scope.errorContainer.add(TypeError("Variable '$identifier' redeclared", varStatement))
        scope.variables[identifier] = varDeclaration
        return varStatement.also { it.span = ctx.toSpan() }
    }
}
