lexer grammar BookishLexer;

BOOK    	: '<book '  -> pushMode(XML_MODE) ;
ARTICLE    	: '<article>' ;
CHAPTER		: '<chapter ' -> pushMode(XML_MODE) ;
SECTION		: '<section ' -> pushMode(XML_MODE) ;
SUBSECTION  : '<subsection ' -> pushMode(XML_MODE) ;
SUBSUBSECTION: '<subsubsection ' -> pushMode(XML_MODE) ;

SIDENOTE	: '<sidenote ' -> pushMode(XML_MODE) ;
END_SIDENOTE: '</sidenote>' ;
CHAPQUOTE	: '<chapquote ' -> pushMode(XML_MODE) ;
SITE      	: '<site '  -> pushMode(XML_MODE) ;
CITATION  	: '<citation ' -> pushMode(XML_MODE) ;
SIDEQUOTE 	: '<sidequote ' -> pushMode(XML_MODE) ;
SIDEFIG 	: '<sidefig' -> pushMode(XML_MODE) ;
END_SIDEFIG : '</sidefig>' ;
FIGURE    	: '<figure ' -> pushMode(XML_MODE) ;
END_FIGURE	: '</figure>' ;

ABSTRACT	: '<abstract>' ;
END_ABSTRACT: '</abstract>' ;
COPYRIGHT	: '<copyright>' ;
END_COPYRIGHT : '</copyright>' ;
DATA        : '<data ' -> pushMode(XML_MODE) ;
END_DATA    : '</data>' ;
DATA_COPY	: '<copy ' -> pushMode(XML_MODE) ;
CSS			: '<css ' -> pushMode(XML_MODE) ;

NOTEBOOK_SUPPORT : '<notebook-support ' -> pushMode(XML_MODE) ;

ASIDE     	: '<aside '  -> pushMode(XML_MODE) ;
END_ASIDE 	: '</aside>' ;

INCLUDE    	: '<include '  -> pushMode(XML_MODE) ;

IMG		    : '<img ' -> pushMode(XML_MODE) ;

CALLOUT     : '<callout ' -> pushMode(XML_MODE) ;
END_CALLOUT : '</callout>' ;

CUT		    : '<cut>' .*? '</cut>' -> skip ;

FIRSTUSE    : '\\first' '{' .*? '}' ;

TODO	    : '\\todo' '{' .*? '}' ;

SYMBOL		: '\\symbol' '{' .*? '}' ;

EQN         : '$' ~'$'+ '$' ;
BLOCK_EQN   : '\\[' .+? '\\]' ;

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

QUOTE : '"' ;

INLINE_CODE : '`' ~[`\r\n]+ '`';

CODEBLOCK : '```' '\n' .*? '\n' '```';

PYEVAL	  : '<pyeval ' {pushMode(PYCODE_MODE);} -> pushMode(XML_MODE) ;
PYFIG	  : '<pyfig '  {pushMode(PYCODE_MODE);} -> pushMode(XML_MODE) ;
INLINE_PY : '<py '     {pushMode(PYCODE_MODE);} -> pushMode(XML_MODE) ;
INLINE_PY_UNLABELED : '<py>' -> pushMode(PYCODE_MODE) ;

LATEX	  : '<latex>' -> pushMode(LATEX_MODE) ;

ITALICS	  : '*' ~' ' '*'
 		  | '*' ~' ' .*? ~' ' '*'
 		  ;
BOLD	  : '**' ~' ' '**'
          | '**' ~' ' .*? ~' ' '**'
          ;

LINK	  : '[' ~']'+ ']' '(' (~')'|'\\)')+ ')' ;
REF		  : '[' ~']'+ ']' ; // like [1] or [chp.intro] or [Ng]

DOLLAR	  : '\\$' ;
LT		  : '\\<' ;
LBRACK 	  : '\\[' ;

COMMENT : '<!--' .*? '-->' -> skip ;

LINE_BREAK : '\\\\' '\r'? '\n' ;

BLANK_LINE : NL ([ \t]* NL)+ ; // at least one blank line (optional junk whitespace on lines)

NL : '\r'? '\n' ;

TAB : '\t' ;

SPACE : ' ' ;

TEXT : NOT_SPECIAL+ ;

fragment
NOT_SPECIAL : ~[$<[*\\"\]`\n\r] ;

// ----------------------------- MODES -------------------------------

mode PYCODE_MODE;
PYCODE_CONTENT : ~'<'+ | '<' ;
END_PYEVAL : '</pyeval>' -> popMode ;
END_PYFIG  : '</pyfig>'  -> popMode ;
END_PY     : '</py>'     -> popMode ;

mode LATEX_MODE;
LATEX_CONTENT : ~'<'+ | '<' ;
END_LATEX : '</latex>' -> popMode ;

mode XML_MODE;           //e.g, <img src="images/neuron.png" alt="neuron.png" width="250">
XML_ATTR : [a-zA-Z\-_0-9]+ ;
XML_EQ : '=' ;
XML_ATTR_VALUE
 	: '"' ('\\"'|~["\n\r])* '"'
 	| '{' ('\\}'|~'}')* '}'
 	| [a-zA-Z0-9.\-~_]+ // catch a few extra char than XML_ATTR but parser must match that too for value
 	;
XML_ATTR_NUM : [0-9]+ ('.' [0-9]*)? ;
XML_WS : [ \t\n]+ -> skip ;
END_TAG : '>' -> popMode ;
