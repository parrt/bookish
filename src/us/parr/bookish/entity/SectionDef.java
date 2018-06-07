package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class SectionDef extends EntityWithScope {
	public SectionDef(int secNumber,
	                  BookishParser.AttrsContext attrsContext,
	                  EntityWithScope enclosingScope)
	{
		super(secNumber, attrsContext, enclosingScope);
	}

	public boolean isGloballyVisible() { return true; }
}
