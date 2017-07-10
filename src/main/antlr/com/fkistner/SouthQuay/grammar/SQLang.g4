grammar SQLang;

@header {
package com.fkistner.SouthQuay.grammar;
}

// Parser

program
    : EOF
    ;

// Lexer

Whitespace: [ \t\r\n]+ -> skip;
