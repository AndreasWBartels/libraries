// Derived from "The Definitive ANTLR 4 Reference" by Terence Parr
// Derived from http://json.org

grammar JSSD;

jssd:   object
    |   objectarray
    ;

objectarray
    :  '[' object (',' object)*']';

object
    :  ( annotation )*  '{' member ( ',' member )* '}'
    |  ( annotation )*  '{'  '}' // empty object
    ;
 
member
    : (annotation)* membername ':' type value? ;

type: '<' classname generics? dimension* '>';

membername
    : STRING;

generics
    : '<' classname (',' classname)* '>';

classname
    : path? name;

dimension
    : '[]';

annotation
    : '@' annotationname ('(' (parameter (',' parameter)*)? ')')?;

annotationname
    : path? name;

parameter
    : name '=' parametervalue;

parametervalue
    :   STRING
    |   INTEGER
    |   NUMBER
    |   'true'  // keywords
    |   'false'
    |   'null'
    ;

path: (NAME '.')+;

array
    :   '[' value (',' value)* ']'
    |   '[' ']' // empty array
    ;

value
    :   STRING
    |   INTEGER
    |   NUMBER
    |   array   // recursion
    |   'true'  // keywords
    |   'false'
    |   'null'
    ;

name: NAME;

STRING : '"' (ESC | ~["\\])* '"' ;
NAME   : (Letter|'_')(Letter|Digit|'_')*;
NUMBER :   '-'? INT '.' [0-9]+ EXP? // 1.35, 1.35E-9, 0.3, -4.5
       |   '-'? INT EXP             // 1e10 -3e4
       |   '-'? INT                 // -3, 45
       ;
INTEGER: INT;

fragment Letter: 'A'..'Z' | 'a'..'z';
fragment Digit : '0'..'9';
fragment ESC : '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;
fragment INT : '0' | [1-9] [0-9]* ; // no leading zeros
fragment EXP : [Ee] [+\-]? INT ; // \- since - means "range" inside [...]

COMMENT     :   '/*' .*? '*/' -> skip ;
LINE_COMMENT:   '//' .*? '\r'? '\n' -> skip ;
WS          :  [ \t\r\n]+ -> skip ;
