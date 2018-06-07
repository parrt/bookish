package us.parr.bookish.semantics;

import us.parr.bookish.entity.ArticleDef;
import us.parr.bookish.entity.BookDef;
import us.parr.bookish.entity.ChapterDef;
import us.parr.bookish.entity.CitationDef;
import us.parr.bookish.entity.EntityDef;
import us.parr.bookish.entity.FigureDef;
import us.parr.bookish.entity.SectionDef;
import us.parr.bookish.entity.SideFigDef;
import us.parr.bookish.entity.SideNoteDef;
import us.parr.bookish.entity.SideQuoteDef;
import us.parr.bookish.entity.SiteDef;
import us.parr.bookish.entity.SubSectionDef;
import us.parr.bookish.entity.SubSubSectionDef;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.BookishParserBaseListener;
import us.parr.bookish.parse.ChapDocInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/** A parse tree listener that defines entities such as figures and chapters.
 *  Python snippets are entities but not defined here;
 *  see {@link DefPythonEntitiesListener}.
 */
public class DefEntitiesListener extends BookishParserBaseListener {
	public ChapDocInfo doc;

	public Map<String,EntityDef> entities = new LinkedHashMap<>();

	public int defCounter = 1;
	public int figCounter = 1; // track 1..n for whole chapter.
	public int secCounter = 1;
	public int subSecCounter = 1;
	public int subSubSecCounter = 1;

	public BookDef currentBook;
	public ArticleDef currentArticle;
	public ChapterDef currentChap;
	public SectionDef currentSec;
	public SectionDef currentSecPtr; //  section, subsection, etc.
	public SubSectionDef currentSubSec;
	public SubSubSectionDef currentSubSubSec;

	public DefEntitiesListener(ChapDocInfo doc) {
		this.doc = doc;
	}

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

	@Override
	public void enterBook(BookishParser.BookContext ctx) {
		currentBook = new BookDef(doc.docNumber, ctx.attrs(),null);
		defEntity(currentBook);
	}

	@Override
	public void enterArticle(BookishParser.ArticleContext ctx) {
		currentArticle = new ArticleDef(doc.docNumber, null,null);
		defEntity(currentArticle);
	}

	@Override
	public void enterChapter(BookishParser.ChapterContext ctx) {
		currentChap = new ChapterDef(doc.docNumber, ctx, null);
		ctx.def = currentChap;
		defEntity(currentChap);
	}

	@Override
	public void enterSection(BookishParser.SectionContext ctx) {
		subSecCounter = 1;
		subSubSecCounter = 1;
		currentSubSec = null;
		currentSubSubSec = null;
		currentSec = new SectionDef(secCounter, ctx.attrs(), currentChap);
		currentSecPtr = currentSec;
		ctx.def = currentSecPtr;
		defEntity(currentSec);
		secCounter++;
	}

	@Override
	public void enterSubsection(BookishParser.SubsectionContext ctx) {
		subSubSecCounter = 1;
		currentSubSubSec = null;
		currentSubSec = new SubSectionDef(subSecCounter, ctx, currentSec);
		currentSecPtr = currentSubSec;
		ctx.def = currentSecPtr;
		defEntity(currentSubSec);
		subSecCounter++;
	}

	@Override
	public void enterSubsubsection(BookishParser.SubsubsectionContext ctx) {
		currentSubSubSec = new SubSubSectionDef(subSubSecCounter, ctx, currentSubSec);
		currentSecPtr = currentSubSubSec;
		ctx.def = currentSecPtr;
		defEntity(currentSubSubSec);
		subSubSecCounter++;
	}

	@Override
	public void enterSite(BookishParser.SiteContext ctx) {
		defEntity(new SiteDef(defCounter++, ctx.attrs()));
	}

	@Override
	public void enterCitation(BookishParser.CitationContext ctx) {
		defEntity(new CitationDef(defCounter++, ctx.attrs()));
	}

	//	sidequote : SIDEQUOTE attrs END_TAG ;
	@Override
	public void enterSidequote(BookishParser.SidequoteContext ctx) {
		defEntity(new SideQuoteDef(defCounter++, ctx.attrs()));
	}

	//	sidenote  : SIDENOTE attrs END_TAG content END_SIDENOTE ;
	@Override
	public void enterSidenote(BookishParser.SidenoteContext ctx) {
		defEntity(new SideNoteDef(defCounter++, ctx.attrs(), ctx.content().getText()));
	}

	//	sidefig   : SIDEFIG attrs END_TAG content END_SIDEFIG ;
	@Override
	public void enterSidefig(BookishParser.SidefigContext ctx) {
		defEntity(new SideFigDef(figCounter++, ctx.attrs()));
	}

	// 	figure    : FIGURE attrs END_TAG content END_FIGURE ;
	@Override
	public void enterFigure(BookishParser.FigureContext ctx) {
		defEntity(new FigureDef(figCounter++, ctx.attrs()));
	}
}
