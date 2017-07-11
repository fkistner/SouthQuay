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
    : number
    ;

number
    : Integer
    | Decimal
    ;

// Lexer

// Keywords
PRINT: 'print';
OUT: 'out';

// Literals
String: '"' (~["])* '"';
Integer: Sign? Digit+;
Decimal: Sign? Digit+ '.' Digit+;

fragment
Digit: [0-9];

fragment
Sign: '-';

// Special
Whitespace: [ \t\r\n]+ -> skip;
Error: .;
