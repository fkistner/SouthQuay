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
    : MINUS? (Integer | Decimal)   # Number
    | expression '+' expression    # Sum
    ;


// Lexer

// Keywords
PRINT: 'print';
OUT: 'out';

// Literals
String: '"' (~["])* '"';
Integer: Digit+;
Decimal: Digit+ '.' Digit+;

fragment
Digit: [0-9];

// Special
Whitespace: [ \t\r\n]+ -> skip;
Error: .;
