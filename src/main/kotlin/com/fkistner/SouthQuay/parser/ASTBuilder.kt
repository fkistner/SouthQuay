package com.fkistner.SouthQuay.parser

import com.fkistner.SouthQuay.grammar.*
import org.antlr.v4.runtime.*

fun SQLangParser.toAST() = this.program().toAST()

fun SQLangParser.ProgramContext.toAST() = Program(statement().map { it.toAST() })

fun SQLangParser.StatementContext.toAST(): Statement {
    return this.accept(object : SQLangBaseVisitor<Statement>() {
        override fun visitPrint(ctx: SQLangParser.PrintContext): Statement {
            return PrintStatement(ctx.string.text.trim('"'))
        }

        override fun visitOut(ctx: SQLangParser.OutContext): Statement {
            return OutStatement(ctx.expr.toAST())
        }

        override fun visitVar(ctx: SQLangParser.VarContext): Statement {
            return VarStatement(ctx.ident.text, ctx.expr.toAST())
        }
    })
}

fun SQLangParser.ExpressionContext.toAST(): Expression {
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
            return Sequence(ctx.from.toAST(), ctx.to.toAST())
        }

        override fun visitMul(ctx: SQLangParser.MulContext): Expression {
            val left = ctx.left.toAST()
            val right = ctx.right.toAST()
            return if (ctx.op.type == SQLangParser.MUL) Mul(left, right) else Div(left, right)
        }

        override fun visitPow(ctx: SQLangParser.PowContext): Expression {
            return Pow(ctx.left.toAST(), ctx.right.toAST())
        }

        override fun visitSum(ctx: SQLangParser.SumContext): Expression {
            val left = ctx.left.toAST()
            val right = ctx.right.toAST()
            return if (ctx.op.type == SQLangParser.PLUS) Sum(left, right) else Sub(left, right)
        }

        override fun visitParen(ctx: SQLangParser.ParenContext): Expression {
            return ctx.expr.toAST()
        }

        override fun visitRef(ctx: SQLangParser.RefContext): Expression {
            return VariableRef(ctx.ident.text)
        }

        override fun visitLam(ctx: SQLangParser.LamContext): Expression {
            return Lambda(ctx.params.map { it.text }, ctx.body.toAST())
        }

        override fun visitFun(ctx: SQLangParser.FunContext): Expression {
            return FunctionInvoc(ctx.`fun`.text, ctx.arg.map { it.toAST() })
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