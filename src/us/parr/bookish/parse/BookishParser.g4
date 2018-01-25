parser grammar BookishParser;

options {
	tokenVocab=BookishLexer;
}

document
	:	chapter BLANK_LINE? EOF
	;

chapter : BLANK_LINE? chap=CHAPTER (section_element|ws)* section* ;

section : BLANK_LINE sec=SECTION (section_element|ws)* subsection* ;

subsection : BLANK_LINE sec=SUBSECTION (section_element|ws)*;

section_element
	:	paragraph
	|	BLANK_LINE? eqn
	|	BLANK_LINE? block_eqn
	|	BLANK_LINE? ordered_list
	|	BLANK_LINE? unordered_list
	|	BLANK_LINE? table
	|	BLANK_LINE? block_image
	|	other
	;

paragraph
	:	BLANK_LINE (paragraph_element|ws)+
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
		( ws? LI ws? list_item )+ ws?
		OL_
	;

unordered_list
	:	UL
		( ws? LI ws? list_item )+ ws?
		UL_
	;

table
	:	TABLE
			( ws? table_header )? // header row
			( ws? table_row )+ // actual rows
			ws?
		TABLE_
	;

table_header : TR ws? (TH table_item)+ ;
table_row : TR ws? (TD table_item)+ ;

list_item : (section_element|ws|BLANK_LINE)* ;

table_item : (section_element|ws|BLANK_LINE)* ;

block_image : IMG attr_assignment+ END_TAG ;

attr_assignment : name=XML_ATTR XML_EQ value=XML_ATTR_VALUE ;

//ws  : (BLANK_LINE | SPACE | NL | TAB)+ ;

ws : (SPACE | NL | TAB)+ ;

link 		:	LINK ;
italics 	:	ITALICS ;
bold 		:	BOLD ;
other       :	OTHER | xml ;

xml			:   XML tagname=XML_ATTR attr_assignment* END_TAG ;

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

