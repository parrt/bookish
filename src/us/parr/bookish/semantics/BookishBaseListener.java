package us.parr.bookish.semantics;

import us.parr.bookish.entity.ChapterDef;
import us.parr.bookish.entity.SectionDef;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.BookishParserBaseListener;

/** This is the base class that tracks the current chapter and current section
 *  for listeners that occur after the {@link DefEntitiesListener}. It
 *  extracts the current pointers from the parse tree that were annotated
 *  during entity definition phase.
 */
public class BookishBaseListener extends BookishParserBaseListener {
	/** Points at the current chapter */
	public ChapterDef currentChap;

	/** Points at either a section, subsection, or subsection */
	public SectionDef currentSecPtr;

	@Override
	public void enterChapter(BookishParser.ChapterContext ctx) {
		currentChap = ctx.def;
	}

	@Override
	public void enterSection(BookishParser.SectionContext ctx) {
		currentSecPtr = ctx.def;
	}

	@Override
	public void enterSubsection(BookishParser.SubsectionContext ctx) {
		currentSecPtr = ctx.def;
	}

	@Override
	public void enterSubsubsection(BookishParser.SubsubsectionContext ctx) {
		currentSecPtr = ctx.def;
	}
}
