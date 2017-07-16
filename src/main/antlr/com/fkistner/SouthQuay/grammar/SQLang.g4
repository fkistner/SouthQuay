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
    : PRINT string=String                       # Print
    | OUT expr=expression                       # Out
    | VAR ident=Identifier EQ expr=expression   # Var
    ;

// Expressions

expression
    : minus=MINUS?  (integer=Integer | real=Real)           # Number
    | fun=Identifier PAREN_LEFT arg+=expression
            (COMMA arg+=expression)* PAREN_RIGHT            # Fun
    | PAREN_LEFT expr=expression PAREN_RIGHT                # Paren
    | <assoc=right> left=expression op=POW right=expression # Pow
    | left=expression op=(MUL  | DIV)   right=expression    # Mul
    | left=expression op=(PLUS | MINUS) right=expression    # Sum
    | SEQ_LEFT from=expression COMMA
                 to=expression SEQ_RIGHT                    # Seq
    | params+=Identifier+ LAM body=expression               # Lam
    | ident=Identifier                                      # Ref
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
LAM:   '->';

PAREN_LEFT:  '(';
PAREN_RIGHT: ')';
SEQ_LEFT:  '{';
SEQ_RIGHT: '}';
COMMA: ',';

// Literals
String: '"' (~["])* '"';
Integer: Digit+;
Real:    Digit+ '.' Digit+;

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
