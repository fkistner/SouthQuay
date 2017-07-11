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
    : print
    | out
    ;

print
    : PRINT String
    ;

out
    : OUT expression
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

// Operators
PLUS:  '+';
MINUS: '-';
MUL:   '*';
DIV:   '/';
POW:   '^';

PAREN_LEFT:  '(';
PAREN_RIGHT: ')';
SEQ_LEFT:  '{';
SEQ_RIGHT: '}';
COMMA: ',';

// Literals
String: '"' (~["])* '"';
Integer: Digit+;
Decimal: Digit+ '.' Digit+;

fragment
Digit: [0-9];

// Special
Whitespace: [ \t\r\n]+ -> skip;
Error: .;
