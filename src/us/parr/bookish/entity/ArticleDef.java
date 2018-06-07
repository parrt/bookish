package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class ArticleDef extends EntityWithScope {
	public ArticleDef(int index,
	                  BookishParser.AttrsContext attrsCtx,
	                  EntityWithScope enclosingScope)
	{
		super(index, attrsCtx, enclosingScope);
	}
}
