package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class BookDef extends EntityWithScope {
	public BookDef(int index,
	               BookishParser.AttrsContext attrsCtx,
	               EntityWithScope enclosingScope)
	{
		super(index, attrsCtx, enclosingScope);
	}
}
