grammar SQLang;

@header {
package com.fkistner.SouthQuay.grammar;
}

// Parser

program
    : statement* EOF
    ;

statement
    : print
    ;

print
    : PRINT String
    ;

// Lexer

// Keywords
PRINT: 'print';

String: '"' (~["])* '"';

Whitespace: [ \t\r\n]+ -> skip;
