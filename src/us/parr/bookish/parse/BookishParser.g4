parser grammar BookishParser;

@header {
import us.parr.bookish.entity.*;
import java.util.Map;
import java.util.List;
}

options {
	tokenVocab=BookishLexer;
}

/** A document is a root document: book or article. All content is
 *  in a chapter in separate file.
 */
rootdocument
	:	book EOF
	|	article EOF
	;

book:	BOOK attrs[List.of("label","author","title","version","watermark")] END_TAG
		(ws? include)*
		(ws? data | ws? notebook_support | ws? copyright | ws? abstract_ | ws? css)*
		book_content
	;

article
	:	ARTICLE
		(ws? include)*
		(ws? data | ws? notebook_support | ws? copyright | ws? abstract_ | ws? css)*
		ws?
	;

/** A chapter doubles as a section of article or is book chapter.
 *  All chapters are in a separate file.
 */
chapter returns [ChapterDef def]
	:	CHAPTER attrs[List.of("label","author","title")] END_TAG
		(ws? abstract_)?
 		content?
 		section*
 		EOF
	;

abstract_ : ABSTRACT content END_ABSTRACT ;

data:	DATA attrs[List.of("dir")] END_TAG
		(ws? dataCopy)*
		ws?
		END_DATA
	;

css : 	CSS attrs[List.of("file")] END_TAG ;

dataCopy : DATA_COPY attrs[List.of("file")] END_TAG ;

copyright : COPYRIGHT content END_COPYRIGHT ;

notebook_support : NOTEBOOK_SUPPORT attrs[List.of("file")] END_TAG ;

book_content : content ;

include : INCLUDE attrs[List.of("file")] END_TAG ;

content
	:	(	paragraph
	 	|	BLANK_LINE
	 	|	callout
	 	|	chapquote
	 	|	sidequote
	 	|	sidenote
	 	|	sidefig
	 	|	figure
	 	|	aside
	 	|	site
	 	|	citation
	 	|	link
	 	|	pyeval
	 	|	pyfig
	 	|	inline_py
		|	block_eqn
		|	eqn
		|	latex
		|	ordered_list
		|	unordered_list
		|	table
		|	block_image
		|	ref
	 	|	italics
	 	|	bold
	 	|	quoted
        |   codeblock
        |	linebreak
		|	todo
		|	symbol
	 	|	text
	 	|	ws
		|	firstuse
		|	inline_code
		|	dollar
		|	lt
	 	)+
	 ;

paragraph returns [List<EntityDef> entitiesRefd = new ArrayList<>()] : BLANK_LINE paragraph_content ;

paragraph_element
	:	eqn
    |	link
    |	italics
    |	bold
    |	image
	|	ref
	|	firstuse
	|	todo
	|	symbol
	|	inline_code
	|	inline_py
	|	linebreak
	|	quoted
 	|	dollar
 	|	lt
	|	text
	|	ws_no_blank_lines
	;

paragraph_content
 	:	paragraph_element+
 	;

dollar : DOLLAR ;

lt : LT ;

link : LINK ;

italics : ITALICS ;

bold : BOLD ;

quoted : QUOTE paragraph_content QUOTE ;

inline_code : INLINE_CODE ;

codeblock : CODEBLOCK ;

linebreak : LINE_BREAK ;

text : TEXT | SPACE | TAB ;

section returns [SectionDef def]
 	:	SECTION attrs[List.of("label","title")] END_TAG
 	  	content?
 	  	subsection*
 	;

subsection returns [SectionDef def]
 	:	SUBSECTION attrs[List.of("label","title")] END_TAG
 	  	content?
 	  	subsubsection*
 	;

subsubsection returns [SectionDef def]
	:	SUBSUBSECTION attrs[List.of("label","title")] END_TAG
		content?
	;

site      : SITE attrs[List.of("label","url")] END_TAG ;

citation  : CITATION attrs[List.of("label","title","author")] END_TAG ;

chapquote : CHAPQUOTE attrs[List.of("quote","author")] END_TAG ;

sidequote : SIDEQUOTE attrs[List.of("label","quote","author")] END_TAG ;

sidenote  : SIDENOTE attrs[List.of("label")] END_TAG content END_SIDENOTE ;

sidefig   : SIDEFIG attrs[List.of("label","caption")] END_TAG content END_SIDEFIG ;

figure    : FIGURE attrs[List.of("label","caption")] END_TAG content END_FIGURE ;

aside	  : ASIDE attrs[List.of("title")] END_TAG content END_ASIDE ;

callout   : CALLOUT attrs[List.of("label")] END_TAG content END_CALLOUT ;

pyeval returns [PyEvalDef codeDef, String stdout, String stderr, String displayData]
    :	PYEVAL attrs[List.of("label","output","hide")] END_TAG pycodeblock END_PYEVAL
	;

pyfig returns [PyFigDef codeDef, String stdout, String stderr]
	:	PYFIG attrs[List.of("label","side","hide","width")] END_TAG pycodeblock END_PYFIG
	;

inline_py returns [InlinePyEvalDef codeDef, String stdout, String stderr, String displayData]
 	:	INLINE_PY attrs[List.of("label")] END_TAG pycodeblock END_PY
 	|	INLINE_PY_UNLABELED pycodeblock END_PY
 	;

pycodeblock : PYCODE_CONTENT* ;

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

table_header : TR (ws? TH attrs[List.of("width")]? END_TAG table_item)+ ;
table_row : TR (ws? TD table_item)+ ;

list_item : content? ;

table_item : content? ;

block_image : image ;

image : IMG attrs[List.of("src","width")] END_TAG ;

ref : REF ;

firstuse : FIRSTUSE ;

todo : TODO ;

symbol : SYMBOL ;

latex returns [String relativeImageFilename] : LATEX LATEX_CONTENT END_LATEX ;

block_eqn returns [String relativeImageFilename] : BLOCK_EQN ;

eqn returns [String relativeImageFilename, float height, float depth]: EQN ;

/** Callers define set of possible/valid attribute names. Checked by
 *  semantic phase not tested here.  The arg effectively annotates the
 *  tree with the valid xml attributes for the caller.
 *
 *  Note: values can be "..." (no newlines) and {...}, which are
 *  interpreted as valid bookish code. Those {...} chunks are
 *  translated during code gen phase.
 */
attrs[List<String> valid] returns [Map<String,String> attributes]
 	:	attr_assignment+
 	;

attr_assignment
	:	name=XML_ATTR XML_EQ value=(XML_ATTR_VALUE|XML_ATTR_NUM|XML_ATTR)
	;

ws : (BLANK_LINE | NL | TAB | SPACE)+ ;

ws_no_blank_lines : (NL | TAB | SPACE)+ ;