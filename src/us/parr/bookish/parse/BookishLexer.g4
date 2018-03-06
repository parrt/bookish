
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
SIDEFIG   : '<sidefig' -> pushMode(XML_MODE) ;
END_SIDEFIG : '</sidefig>' ;
FIGURE    : '<figure' -> pushMode(XML_MODE) ;
END_FIGURE: '</figure>' ;

ASIDE     : '<aside'  -> pushMode(XML_MODE) ;
END_ASIDE : '</aside>' ;

CALLOUT   : '\\callout' ;

CUT		  : '\\cut' '{' .*? '\r'? '\n' '}' '\r'? '\n' -> skip ;

FIRSTUSE  : '\\first' ;

TODO	  : '\\todo' | '\\TODO' ;

PYEVAL	  : '<pyeval' ARG* '>' -> pushMode(PYCODE) ;
PYFIG	  : '<pyfig' ARG* '>' -> pushMode(PYCODE) ;

// This stuff parsed later (again) by XML.g4
fragment
ARG : [ \r\n\t]* ATTR [ \r\n\t]* '=' [ \r\n\t]* (ATTR|ATTR_VALUE|ATTR_NUM) [ \r\n\t]* ;
fragment
ATTR : [a-zA-Z]+  ;
fragment
ATTR_VALUE : '"' ('\\"'|~'"')* '"' ;
fragment
ATTR_NUM : [0-9]+ ('.' [0-9]*)? ;

AUTHOR	  : '\\author' ;
PREABSTRACT  : '\\preabstract' ;
ABSTRACT  : '\\abstract' ;

SYMBOL    : '\\symbol' ;

LINK	  : '[' ~']'+ ']' '(' (~')'|'\\)')+ ')' ;
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

INLINE_CODE : '`' ~[`\r\n]+ '`';

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

DOLLAR	  : '\\$' ;
EQN       : '$' ~'$'+ '$' ;
BLOCK_EQN : '\\[' .+? '\\]' ;

/** Block of random latex show as block style content (not inline)
\latex{{
...
}}
*/
LATEX	  : '\\latex{{' '\r'? '\n' .*? '\r'? '\n}}' ;

LINE_BREAK : '\\\\' '\r'? '\n' ;

BLANK_LINE : NL ([ \t]* NL)+ ; // at least one blank line (optional junk whitespace on lines)

TAB : '\t' ;
SPACE : ' ' ;
NL : '\r'? '\n' ;

OTHER : NOT_SPECIAL+ ;

COMMENT : '<!--' .*? '-->' -> skip ;

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
CODE_BLOCK_ARGS_WS : [ \r\t\n]+ -> skip ;
END_CODE_BLOCK_ARGS : ']' -> popMode ;

mode CODE_BLOCK_MODE;
END_CODE_BLOCK : '\r'? '\n' '}' -> popMode ;
CODE_BLOCK_STUFF : ~[\r\n}]+ ;
CODE_BLOCK_OTHER : [\r\n}] ; // match curly when not on left edge

mode XML_MODE;           //e.g, <img src="images/neuron.png" alt="neuron.png" width="250">
XML_ATTR : [a-zA-Z]+ ;
XML_EQ : '=' ;
XML_ATTR_VALUE : '"' ('\\"'|~'"')* '"' ;
XML_ATTR_NUM : [0-9]+ ('.' [0-9]*)? ;
XML_WS : [ \t]+ -> skip ;
END_OF_TAG : '>' -> popMode ;

mode PYCODE;

PYCODE_CONTENT : ~'<'+ | '<' ;
END_PYEVAL : '</pyeval>' -> popMode ;
END_PYFIG  : '</pyfig>' -> popMode ;
