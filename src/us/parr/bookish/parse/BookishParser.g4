parser grammar BookishParser;

@header {
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Arrays;

import us.parr.lib.ParrtStrings;
import us.parr.lib.ParrtCollections;
import us.parr.lib.ParrtIO;
import us.parr.bookish.model.entity.*;
import static us.parr.bookish.translate.Translator.splitSectionTitle;
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
		if ( entity.label!=null ) {
			if ( entities.containsKey(entity.label) ) {
				System.err.printf("line %d: redefinition of label %s\n",
				 entity.getStartToken().getLine(), entity.label);
			}
			entities.put(entity.label, entity);
			System.out.println("Def "+entity);
		}
	}

	// Each parser (usually per doc/chapter) keeps its own counts for sections, figures, sidenotes, web links, ...

	public int defCounter = 1;
	public int figCounter = 1; // track 1..n for whole chapter.
	public int secCounter = 1;
	public int subSecCounter = 1;
	public int subSubSecCounter = 1;

	public ChapterDef currentChap;
	public SectionDef currentSec;
	public SubSectionDef currentSubSec;
	public SubSubSectionDef currentSubSubSec;

	public List<ExecutableCodeDef> codeBlocks = new ArrayList<>();
	public int codeCounter = 1;

	public String inputFilename;
	public int chapNumber;

	public BookishParser(TokenStream input, String inputFilename, int chapNumber) {
		this(input);
		this.inputFilename = inputFilename;
		this.chapNumber = chapNumber;
	}
}

document
	:	chapter BLANK_LINE? EOF
	;

chapter : BLANK_LINE? chap=CHAPTER author? preabstract? abstract_? (section_element|ws)* section*
		  {
		  currentChap = new ChapterDef(chapNumber, $chap, null);
		  defEntity(currentChap);
		  }
		;

author : (ws|BLANK_LINE)? AUTHOR LCURLY paragraph_optional_blank_line RCURLY ;

abstract_ : (ws|BLANK_LINE)? ABSTRACT LCURLY paragraph_optional_blank_line paragraph* RCURLY;

preabstract : (ws|BLANK_LINE)? PREABSTRACT LCURLY paragraph_optional_blank_line paragraph* RCURLY;

section : BLANK_LINE sec=SECTION (section_element|ws)* subsection*
		  {
		  subSecCounter = 1;
		  subSubSecCounter = 1;
		  currentSubSec = null;
		  currentSubSubSec = null;
		  currentSec = new SectionDef(secCounter, $sec, currentChap);
		  defEntity(currentSec);
		  secCounter++;
		  }
		;

subsection : BLANK_LINE sec=SUBSECTION (section_element|ws)* subsubsection*
		  {
		  subSubSecCounter = 1;
		  currentSubSubSec = null;
		  currentSubSec = new SubSectionDef(subSecCounter, $sec, currentSec);
		  defEntity(currentSubSec);
		  subSecCounter++;
		  }
		;

subsubsection : BLANK_LINE sec=SUBSUBSECTION (section_element|ws)*
		  {
		  currentSubSubSec = new SubSubSectionDef(subSubSecCounter, $sec, currentSubSec);
		  defEntity(currentSubSubSec);
		  subSubSecCounter++;
		  }
		;

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
		|	pycode
		|	pyfig
		|	pyeval
		)
	|	other
	;

site      : SITE REF ws? block
			{defEntity(new SiteDef(defCounter++, $REF, $block.text));}
		  ;

citation  : CITATION REF ws? t=block ws? a=block
			{defEntity(new CitationDef(defCounter++, $REF, $t.text, $a.text));}
		  ;

chapquote : CHAPQUOTE q=block ws? a=block
		  ;

sidequote : SIDEQUOTE (REF ws?)? q=block ws? a=block
			{if ($REF!=null) defEntity(new SideQuoteDef(defCounter++, $REF, $q.text, $a.text));}
		  ;

sidenote  : CHAPQUOTE (REF ws?)? block
			{if ($REF!=null) defEntity(new SideNoteDef(defCounter++, $REF, $block.text));}
		  ;

sidefig   : SIDEFIG REF? ws? code=block (ws? caption=block)?
			{if ($REF!=null) defEntity(new SideFigDef(figCounter++, $REF, $code.text, $caption.text));}
		  ;

figure    : FIGURE REF? ws? code=block (ws? caption=block)?
			{if ($REF!=null) defEntity(new FigureDef(figCounter++, $REF, $code.text, $caption.text));}
		  ;

pycode    : CODEBLOCK ;

pyfig returns [PyFigDef codeDef, String stdout, String stderr]
	:	PYFIG codeblock_args? ws? code=codeblock END_CODE_BLOCK
		{
		String fname = ParrtIO.basename(inputFilename);
		String py = $code.text.trim();
		if ( py.length()>0 ) {
			$codeDef = new PyFigDef($ctx, fname, codeCounter, $codeblock_args.argMap, py);
			codeBlocks.add($codeDef);
		}
		codeCounter++;
		}
	;

/** \pyeval[env]{code to exec}{expr to display} */
pyeval returns [PyEvalDef codeDef, String stdout, String stderr, String displayData]
    :	PYEVAL codeblock_args? ws? code=codeblock END_CODE_BLOCK ws? b=block?
		{
		String fname = ParrtIO.basename(inputFilename);
		// last line is expression to get output or blank line or comment
		String py = $code.text.trim();
		if ( py.length()>0 ) {
			String outputExpr = null;
			if ( $b.ctx!=null ) {
				outputExpr = $b.ctx.paragraph_content().getText();
			}
			$codeDef = new PyEvalDef($ctx, fname, codeCounter, $codeblock_args.argMap, py, outputExpr);
			codeBlocks.add($codeDef);
		}
		codeCounter++;
		}
	;

codeblock
	:	(CODE_BLOCK_STUFF|CODE_BLOCK_OTHER)+
	;

/** \pyfig[label,hide=true,width="20em"]{...}
 *  \pyfig[width="20em"]{...}
 *  \pyfig[label]{...}
 */
codeblock_args returns [Map<String,String> argMap = new LinkedHashMap<>()]
	:	START_CODE_BLOCK_ARGS
		(	l=CODE_BLOCK_ATTR CODE_BLOCK_COMMA
			codeblock_arglist[$argMap] ( CODE_BLOCK_COMMA codeblock_arglist[$argMap] )*
			{$argMap.put("label", $l.text.trim());}

		|	l=CODE_BLOCK_ATTR
			{$argMap.put("label", $l.text.trim());}

   		|	codeblock_arglist[$argMap] ( CODE_BLOCK_COMMA codeblock_arglist[$argMap] )*
		)
		END_CODE_BLOCK_ARGS
	;

codeblock_arglist[Map<String,String> argMap]
	:	name=CODE_BLOCK_ATTR CODE_BLOCK_EQ (value=CODE_BLOCK_ATTR_VALUE|value=CODE_BLOCK_ATTR)
    	{
    	String v = $value.text;
    	if ( v.startsWith("\"") ) v = ParrtStrings.stripQuotes(v);
    	$argMap.put($name.text,v);
    	}
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
	|	firstuse
	|	todo
	|	inline_code
	|	other
	;

ref : REF ;

symbol : SYMBOL block ; // e.g., \symbol{degree}, \symbol{tm}

quoted : QUOTE (paragraph_element|ws)+ QUOTE ;

inline_code : INLINE_CODE ;

firstuse : FIRSTUSE block ;

todo : TODO block ;

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

attrs returns [Map<String,String> attrMap = new LinkedHashMap<>()] : attr_assignment[$attrMap]* ;

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
