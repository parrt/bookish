lexer grammar BookishLexer;

SUBSECTION: '###' ~'\n'+ ;
SECTION   : '##' ~'\n'+ ;
CHAPTER   : '#' ~'\n'+ ;

AUTHOR	  : '[author]' ;
PREABSTRACT  : '[preabstract]' ;
ABSTRACT  : '[abstract]' ;

LINK	  : '[' ~']'+ ']' '(' ~')'+ ')' ;
ITALICS	  : '*' ~' ' '*'
 		  | '*' ~' ' .*? ~' ' '*'
 		  ;
BOLD	  : '**' ~' ' '**'
          | '**' ~' ' .*? ~' ' '**'
          ;

IMG		  : '<img ' -> pushMode(XML_MODE) ;
XML		  : '<' -> pushMode(XML_MODE) ;

OL : '<ol>' ;
LI : '<li>' ;
OL_ : '</ol>' ;

UL : '<ul>' ;
UL_ : '</ul>' ;

TABLE : '<table>' ;
TR : '<tr>' ;
TD : '<td>' ;
TH : '<th>' ;
TABLE_ : '</table>' ;

END_TAG	  : '</' [a-zA-Z_][a-zA-Z0-9_]* '>' ;

EQN       : '$' ~'$'+ '$' ;
BLOCK_EQN : '\\\\[' .+? '\\\\]' ;

/** Block of random latex show as block style content (not inline)
\latex{{
...
}}
*/
LATEX	  : '\\latex{{' '\r'? '\n' .*? '\r'? '\n}}' ;

BLANK_LINE : NL ([ \t]* NL)+ ; // at least one blank line (optional junk whitespace on lines)

TAB : '\t' ;
SPACE : ' ' ;
NL : '\r'? '\n' ;

OTHER : NOT_SPECIAL+ ;

fragment
NOT_SPECIAL : ~[$<#[*\\\n] ;

mode XML_MODE ;           //e.g, <img src="images/neuron.png" alt="neuron.png" width="250">
XML_ATTR : [a-zA-Z]+ ;
XML_EQ : '=' ;
XML_ATTR_VALUE : '"' .*? '"' ;
XML_WS : [ \t]+ -> skip ;
END_OF_TAG : '>' -> popMode ;

/*
mode EQN_MODE ;
EQN_UNDERSCORE : '_' ;
EQN_NL : '\n' {System.err.println("newline in $...$ at line "+getLine());} ;
END_EQN : '$' -> popMode ;
EQN_OTHER : . ;

mode BLOCK_EQN_MODE ;
BLOCK_EQN_AMP : '&' ;
BLOCK_EQN_UNDERSCORE : '_' ;
BLOCK_EQN_END_ROW : '\\\\' ;
END_BLOCK_EQN : '\\\\]' -> popMode ;
BLOCK_EQN_OTHER : . ;
*/