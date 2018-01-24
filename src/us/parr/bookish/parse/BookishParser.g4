parser grammar BookishParser;

options {
	tokenVocab=BookishLexer;
}

document
	:	chapter EOF
	;

chapter : BLANK_LINE? chap=CHAPTER section_element* section* ;

section : BLANK_LINE sec=SECTION section_element* subsection* ;

subsection : BLANK_LINE sec=SUBSECTION section_element*;

section_element
	:	paragraph
	|	block_eqn
	;

paragraph
	:	BLANK_LINE paragraph_element*
	;

paragraph_element
	:	eqn
    |	link
    |	italics
    |	bold
	|	other
	;

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

