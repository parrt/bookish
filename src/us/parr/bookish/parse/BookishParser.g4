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
	|	eqn
	|	block_eqn
	|	ordered_list
	|	other
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

ordered_list
	:	OL
		( ws? LI list_item )+ ws?
		OL_
	;

list_item : (section_element|BLANK_LINE)* ;

ws  : (BLANK_LINE | SPACE | NL | TAB)+ ;

link 		:	LINK ;
italics 	:	ITALICS ;
bold 		:	BOLD ;
other       :	OTHER | SPACE | NL | TAB | XML_TAG ;

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

