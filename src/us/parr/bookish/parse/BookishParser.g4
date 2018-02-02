parser grammar BookishParser;

@header {
import java.util.Map;
import java.util.HashMap;
import us.parr.lib.ParrtStrings;
}

options {
	tokenVocab=BookishLexer;
}

document
	:	chapter BLANK_LINE? EOF
	;

chapter : BLANK_LINE? chap=CHAPTER author? preabstract? abstract_? (section_element|ws)* section* ;

author : BLANK_LINE? AUTHOR paragraph ;

abstract_ : BLANK_LINE? ABSTRACT paragraph+;

preabstract : BLANK_LINE? PREABSTRACT paragraph+;

section : BLANK_LINE sec=SECTION (section_element|ws)* subsection* ;

subsection : BLANK_LINE sec=SUBSECTION (section_element|ws)* subsubsection*;

subsubsection : BLANK_LINE sec=SUBSUBSECTION (section_element|ws)*;

section_element
	:	paragraph
	|	BLANK_LINE? link
	|	BLANK_LINE? eqn
	|	BLANK_LINE? block_eqn
	|	BLANK_LINE? ordered_list
	|	BLANK_LINE? unordered_list
	|	BLANK_LINE? table
	|	BLANK_LINE? block_image
	|	BLANK_LINE? latex
	|	xml
	|	other
	;

paragraph
	:	BLANK_LINE (paragraph_element|quoted|ws)+
	;

paragraph_element
	:	eqn
    |	link
    |	italics
    |	bold
	|	xml
	|	other
	;

quoted : QUOTE (paragraph_element|ws)+ QUOTE ;

latex : LATEX ;

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

table_header : TR ws? (TH attrs END_OF_TAG table_item)+ ;
table_row : TR ws? (TD table_item)+ ;

list_item : (section_element|paragraph_element|quoted|ws|BLANK_LINE)* ;

table_item : (section_element|paragraph_element|quoted|ws|BLANK_LINE)* ;

block_image : IMG attrs END_OF_TAG ;

attrs returns [Map<String,String> attrMap = new HashMap<>()] : attr_assignment[$attrMap]* ;

attr_assignment[Map<String,String> attrMap]
	:	name=XML_ATTR XML_EQ value=XML_ATTR_VALUE
		{$attrMap.put($name.text,ParrtStrings.stripQuotes($value.text));}
	;

xml	: XML tagname=XML_ATTR attrs END_OF_TAG | END_TAG ;

link 		:	LINK ;
italics 	:	ITALICS ;
bold 		:	BOLD ;
other       :	OTHER | POUND ;

block_eqn : BLOCK_EQN ;

eqn : EQN ;

ws : (SPACE | NL | TAB)+ ;
