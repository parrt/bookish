package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class SubSectionDef extends SectionDef {
	public SubSectionDef(int secNumber,
	                     BookishParser.SubsectionContext ctx,
	                     EntityWithScope enclosingScope)
	{
		super(secNumber, ctx.attrs(), enclosingScope);
	}
}
