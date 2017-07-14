package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*
import org.antlr.v4.runtime.*

fun SQLangParser.toAST() = this.program().toAST()

fun SQLangParser.ProgramContext.toAST(): Program {
    val scope = Scope()
    return Program(statement().map { it.toAST(scope) }, scope)
}

fun SQLangParser.StatementContext.toAST(scope: Scope): Statement {
    return this.accept(object : SQLangBaseVisitor<Statement>() {
        override fun visitPrint(ctx: SQLangParser.PrintContext): Statement {
            return PrintStatement(ctx.string.text.trim('"'))
        }

        override fun visitOut(ctx: SQLangParser.OutContext): Statement {
            return OutStatement(ctx.expr.toAST(scope))
        }

        override fun visitVar(ctx: SQLangParser.VarContext): Statement {
            val identifier = ctx.ident.text
            val varStatement = VarStatement(identifier, ctx.expr.toAST(scope))
            scope.variables[identifier] = varStatement
            return varStatement
        }
    })
}

fun SQLangParser.ExpressionContext.toAST(scope: Scope): Expression {
    return this.accept(object : SQLangBaseVisitor<Expression>() {
        override fun visitNumber(ctx: SQLangParser.NumberContext): Expression {
            ctx.integer?.let {
                val value = it.text.toInt()
                return IntegerLiteral(if (ctx.minus == null) value else value.unaryMinus())
            }
            ctx.real.let {
                val value = it.text.toDouble()
                return RealLiteral(if (ctx.minus == null) value else value.unaryMinus())
            }
        }

        override fun visitSeq(ctx: SQLangParser.SeqContext): Expression {
            return Sequence(ctx.from.toAST(scope), ctx.to.toAST(scope))
        }

        override fun visitMul(ctx: SQLangParser.MulContext): Expression {
            val left = ctx.left.toAST(scope)
            val right = ctx.right.toAST(scope)
            return if (ctx.op.type == SQLangParser.MUL) Mul(left, right) else Div(left, right)
        }

        override fun visitPow(ctx: SQLangParser.PowContext): Expression {
            return Pow(ctx.left.toAST(scope), ctx.right.toAST(scope))
        }

        override fun visitSum(ctx: SQLangParser.SumContext): Expression {
            val left = ctx.left.toAST(scope)
            val right = ctx.right.toAST(scope)
            return if (ctx.op.type == SQLangParser.PLUS) Sum(left, right) else Sub(left, right)
        }

        override fun visitParen(ctx: SQLangParser.ParenContext): Expression {
            return ctx.expr.toAST(scope)
        }

        override fun visitRef(ctx: SQLangParser.RefContext): Expression {
            val identifier = ctx.ident.text
            return VariableRef(identifier, scope.variables[identifier])
        }

        override fun visitLam(ctx: SQLangParser.LamContext): Expression {
            return Lambda(ctx.params.map { it.text }, ctx.body.toAST(scope))
        }

        override fun visitFun(ctx: SQLangParser.FunContext): Expression {
            val identifier = ctx.`fun`.text
            return FunctionInvoc(identifier, ctx.arg.map { it.toAST(scope) }, scope.functions[identifier])
        }
    })
}

object ASTBuilder {
    fun parseStream(charStream: CharStream, errorContainer: MutableList<SQLangError> = mutableListOf()): Program? {
        val errorListener = object : BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                errorContainer.add(SyntaxError(msg, offendingSymbol, line, charPositionInLine))
            }
        }

        val lexer = SQLangLexer(charStream)
        lexer.addErrorListener(errorListener)
        val parser = SQLangParser(CommonTokenStream(lexer))
        parser.addErrorListener(errorListener)

        val program = parser.program()

        if (errorContainer.count() > 0) {
            return null
        }
        return program.toAST()
    }
}