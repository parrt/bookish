package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class ChapterDef extends EntityWithScope {
	public ChapterDef(int chapNumber,
	                  BookishParser.ChapterContext ctx,
	                  EntityWithScope enclosingScope)
	{
		super(chapNumber, ctx.attrs(), enclosingScope);
	}

	public boolean isGloballyVisible() { return true; }
}
