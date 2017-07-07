grammar TestLang;

@header {
package com.fkistner.research.ParserPOC;
}

// Parser

program
        : statement* EOF
        ;

statement
        : print
        ;

print
        : Print String
        ;


// Lexer

Print: 'print';

String: Quote StringChar* Quote;

Quote: '"';

fragment
StringChar: ~["];

Whitespace: [ \t\r\n]+ -> skip;
