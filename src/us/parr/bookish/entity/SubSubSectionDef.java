package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class SubSubSectionDef extends SectionDef {
	public SubSubSectionDef(int secNumber,
	                        BookishParser.SubsubsectionContext ctx,
	                        EntityWithScope enclosingScope)
	{
		super(secNumber, ctx.attrs(), enclosingScope);
	}
}
