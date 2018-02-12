
lexer grammar BookishLexer;

SUBSUBSECTION: '#### ' ~'\n'+ {_tokenStartCharPositionInLine==0}? ;
SUBSECTION: '### ' ~'\n'+ {_tokenStartCharPositionInLine==0}? ;
SECTION   : '## ' ~'\n'+ {_tokenStartCharPositionInLine==0}? ;
CHAPTER   : '# ' ~'\n'+ {_tokenStartCharPositionInLine==0}? ;

POUND	  : '#'+ {_tokenStartCharPositionInLine!=0}? ;

SITE      : '\\site' ;
CITATION  : '\\citation' ;
SIDEQUOTE : '\\sidequote' ;
CHAPQUOTE : '\\chapquote' ;
SIDENOTE  : '\\sidenote' ;
SIDEFIG   : '\\sidefig' ;
FIGURE    : '\\figure' ;

FIRSTUSE  : '\\first' ;

TODO	  : '\\todo' | '\\TODO' ;

PYFIG	  : '\\pyfig' -> pushMode(CODE_BLOCK_START_MODE) ;
PYEVAL	  : '\\pyeval' -> pushMode(CODE_BLOCK_START_MODE) ;

AUTHOR	  : '\\author' ;
PREABSTRACT  : '\\preabstract' ;
ABSTRACT  : '\\abstract' ;

SYMBOL    : '\\symbol' ;

LINK	  : '[' ~']'+ ']' '(' ~')'+ ')' ;
ITALICS	  : '*' ~' ' '*'
 		  | '*' ~' ' .*? ~' ' '*'
 		  ;
BOLD	  : '**' ~' ' '**'
          | '**' ~' ' .*? ~' ' '**'
          ;

IMG		  : '<img ' -> pushMode(XML_MODE) ;

REF : '[' ~']'+ ']' ; // like [1] or [chp.intro] or [Ng]

LCURLY : '{' ;
RCURLY : '}' ;

QUOTE : '"' ;

BACKTICK : '`' ;

CODEBLOCK : '```' '\n' .*? '\n' '```';


OL : '<ol>' ;
LI : '<li>' ;
OL_ : '</ol>' ;

UL : '<ul>' ;
UL_ : '</ul>' ;

TABLE : '<table>' ;
TR : '<tr>' ;
TH : '<th' -> pushMode(XML_MODE) ;
TD : '<td>' ;
TABLE_ : '</table>' ;

XML		  : '<' -> pushMode(XML_MODE) ;
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
NOT_SPECIAL : ~[$<#[*\\\n"{}\]`] ;

mode CODE_BLOCK_START_MODE;
START_CODE_BLOCK_ARGS : '[' -> pushMode(CODE_BLOCK_ARGS) ;
START_CODE_BLOCK      : '{' -> mode(CODE_BLOCK_MODE), skip ;

mode CODE_BLOCK_ARGS;
CODE_BLOCK_ATTR : [a-zA-Z]+ ;
CODE_BLOCK_EQ : '=' ;
CODE_BLOCK_ATTR_VALUE : '"' .*? '"' ;
CODE_BLOCK_COMMA : ',' ;
END_CODE_BLOCK_ARGS : ']' -> popMode ;

mode CODE_BLOCK_MODE;
END_CODE_BLOCK : '\r'? '\n' '}' -> popMode ;
CODE_BLOCK_STUFF : ~[\r\n}]+ ;
CODE_BLOCK_OTHER : [\r\n}] ; // match curly when not on left edge

mode XML_MODE;           //e.g, <img src="images/neuron.png" alt="neuron.png" width="250">
XML_ATTR : [a-zA-Z]+ ;
XML_EQ : '=' ;
XML_ATTR_VALUE : '"' .*? '"' ;
XML_WS : [ \t]+ -> skip ;
END_OF_TAG : '>' -> popMode ;
