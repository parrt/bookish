lexer grammar BookishLexer;

BLOCK_EQN : '\\\\[' -> pushMode(EQN_MODE) ;

OTHER : . ;

mode EQN_MODE ;

AMP : '&' ;
UNDERSCORE : '_' ;
EQN_OTHER : . ;

BLOCK_EQN_END : '\\\\]' -> popMode ;
