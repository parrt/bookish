parser grammar BookishParser;

options {
	tokenVocab=BookishLexer;
}

document
	:	chapter
		(section|element|paragraph)* ;

element
	:	block_eqn
	|	eqn
    |	link
    |	italics
    |	bold
	|	other
	;

paragraph
	:	PARA element* (PARA|EOF)
	;

chapter 	:	CHAPTER element* ;
section 	:	SECTION element* ;
subsection 	:	SUBSECTION element*;
link 		:	LINK ;
italics 	:	ITALICS ;
bold 		:	BOLD ;
other       :	OTHER ;

block_eqn : BLOCK_EQN block_eqn_content END_BLOCK_EQN ;

block_eqn_content : block_eqn_element* ;

block_eqn_element
	:	BLOCK_EQN_AMP
	|	BLOCK_EQN_UNDERSCORE
	|	BLOCK_EQN_END_ROW
	|	BLOCK_EQN_OTHER
	;

eqn : EQN eqn_content END_EQN ;

eqn_content : eqn_element* ;

eqn_element
	:	EQN_UNDERSCORE
	|	EQN_NL
	|	EQN_OTHER
	;

