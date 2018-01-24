lexer grammar BookishLexer;

CHAPTER   : '#' ~'\n'+ ;
SECTION   : '##' ~'\n'+ ;
SUBSECTION: '###' ~'\n'+ ;

LINK	  : '[' .*? ']' '(' .*? ')' ;
ITALICS	  : '*' ~' ' .*? ~' ' '*' ;
BOLD	  : '**' ~' ' .*? ~' ' '**' ;

EQN       : '$'     -> pushMode(EQN_MODE) ;
BLOCK_EQN : '\\\\[' -> pushMode(BLOCK_EQN_MODE) ;

PARA	  : '\n\n' ;

OTHER : . ;

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
