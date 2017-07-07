grammar TestLang;

Whitespace
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;

program
        : print | program print
        ;

print
        :   'print' '"' ~('"')+ '"'
        ;
