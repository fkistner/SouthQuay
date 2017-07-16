package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*

class StatementASTBuilder(val scope: Scope): SQLangBaseVisitor<Statement>() {
    override fun visitPrint(ctx: SQLangParser.PrintContext): Statement {
        return PrintStatement(ctx.string.text.trim('"'))
    }

    override fun visitOut(ctx: SQLangParser.OutContext): Statement {
        return OutStatement(ctx.expr.toAST(scope))
    }

    override fun visitVar(ctx: SQLangParser.VarContext): Statement {
        val identifier = ctx.ident.text
        val expression = ctx.expr.toAST(scope)
        val varDeclaration = VarDeclaration(identifier, expression.type)
        val varStatement = VarStatement(varDeclaration, expression)
        if (scope.variables.containsKey(identifier)) scope.errorContainer.add(TypeError("Variable '$identifier' redeclared", varStatement))
        scope.variables[identifier] = varDeclaration
        return varStatement
    }
}
