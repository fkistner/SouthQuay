grammar SQLang;

@header {
package com.fkistner.SouthQuay.grammar;
}

// Parser

program
    : statement* EOF
    ;

// Statements

statement
    : PRINT String                  # Print
    | OUT expression                # Out
    | VAR Identifier EQ expression  # Var
    ;

// Expressions

expression
    : MINUS?  (Integer | Decimal)           # Number
    | PAREN_LEFT expression PAREN_RIGHT     # Paren
    | expression  POW           expression  # Pow
    | expression (MUL  | DIV)   expression  # Mul
    | expression (PLUS | MINUS) expression  # Sum
    | SEQ_LEFT expression COMMA expression SEQ_RIGHT # Seq
    ;


// Lexer

// Keywords
PRINT: 'print';
OUT:   'out';
VAR:   'var';

// Operators
PLUS:  '+';
MINUS: '-';
MUL:   '*';
DIV:   '/';
POW:   '^';

EQ:    '=';

PAREN_LEFT:  '(';
PAREN_RIGHT: ')';
SEQ_LEFT:  '{';
SEQ_RIGHT: '}';
COMMA: ',';

// Literals
String: '"' (~["])* '"';
Integer: Digit+;
Decimal: Digit+ '.' Digit+;

Identifier: (Letter | Special) (Digit | Letter | Special)*;

fragment
Digit: [0-9];

fragment
Letter: [a-zA-Z];

fragment
Special: '_';

// Special
Whitespace: [ \t\r\n]+ -> skip;
Error: .;
