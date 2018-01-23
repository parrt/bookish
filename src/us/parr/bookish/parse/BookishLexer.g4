lexer grammar BookishLexer;

EQN       : '$'     -> pushMode(EQN_MODE) ;
BLOCK_EQN : '\\\\[' -> pushMode(BLOCK_EQN_MODE) ;

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
