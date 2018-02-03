parser grammar BookishParser;

@header {
import java.util.Map;
import java.util.HashMap;
import us.parr.lib.ParrtStrings;
import us.parr.bookish.model.entity.*;
}

options {
	tokenVocab=BookishLexer;
}

@members {
	/** Global labeled entities such as citations, figures, websites.
	 *  Collected from all input markdown files.
	 *
	 *  Track all labeled entities in this file for inclusion in overall book.
	 *  Do during parse for speed, to avoid having to walk tree 2x.
	 */
	public Map<String,EntityDef> entities = new HashMap<>();

	public void defEntity(EntityDef entity) {
		entities.put(entity.label, entity);
	}

	/** Each parser (usually per doc/chapter) keeps its own count of refs: 1, 2, ... */
	public int defCounter = 1;
}

document
	:	chapter BLANK_LINE? EOF
	;

chapter : BLANK_LINE? chap=CHAPTER author? preabstract? abstract_? (section_element|ws)* section* ;

author : (ws|BLANK_LINE)? AUTHOR LCURLY paragraph_optional_blank_line RCURLY ;

abstract_ : (ws|BLANK_LINE)? ABSTRACT LCURLY paragraph_optional_blank_line paragraph* RCURLY;

preabstract : (ws|BLANK_LINE)? PREABSTRACT LCURLY paragraph_optional_blank_line paragraph* RCURLY;

section : BLANK_LINE sec=SECTION (section_element|ws)* subsection* ;

subsection : BLANK_LINE sec=SUBSECTION (section_element|ws)* subsubsection*;

subsubsection : BLANK_LINE sec=SUBSUBSECTION (section_element|ws)*;

section_element
	:	paragraph
	|	BLANK_LINE?
	 	(	link
		|	eqn
		|	block_eqn
		|	ordered_list
		|	unordered_list
		|	table
		|	block_image
		|	latex
		|	xml
		|	site
		|	citation
		|	sidequote
		|	sidenote
		|	chapquote
		|	sidefig
		|	figure
		)
	|	other
	;

site      : SITE REF ws? block
			{defEntity(new SiteDef(defCounter++, $REF.text, $block.text));}
		  ;

citation  : CITATION REF ws? t=block ws? a=block
			{defEntity(new CitationDef(defCounter++, $REF.text, $t.text, $a.text));}
		  ;

chapquote : CHAPQUOTE q=block ws? a=block
		  ;

sidequote : SIDEQUOTE (REF ws?)? q=block ws? a=block
			{if ($REF!=null) defEntity(new SideQuoteDef(defCounter++, $REF.text, $q.text, $a.text));}
		  ;

sidenote  : CHAPQUOTE (REF ws?)? block
			{if ($REF!=null) defEntity(new SideNoteDef(defCounter++, $REF.text, $block.text));}
		  ;

sidefig   : SIDEFIG REF? ws? code=block (ws? caption=block)?
			{if ($REF!=null) defEntity(new SideFigDef(defCounter++, $REF.text, $code.text, $caption.text));}
		  ;

figure    : FIGURE REF? ws? code=block (ws? caption=block)?
			{if ($REF!=null) defEntity(new FigureDef(defCounter++, $REF.text, $code.text, $caption.text));}
		  ;

block : LCURLY paragraph_content? RCURLY ;

paragraph
	:	BLANK_LINE paragraph_content
	;

paragraph_optional_blank_line
	:	BLANK_LINE? paragraph_content
	;

paragraph_content
	:	(paragraph_element|quoted|ws)+
	;

paragraph_element
	:	eqn
    |	link
    |	italics
    |	bold
    |	image
	|	xml
	|	ref
	|	symbol
	|	other
	;

ref : REF ;

symbol : SYMBOL REF ; // e.g., \symbol[degree], \symbol[tm]

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

block_image : image ;

image : IMG attrs END_OF_TAG ;

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
