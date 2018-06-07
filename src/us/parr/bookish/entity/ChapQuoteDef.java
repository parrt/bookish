package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class ChapQuoteDef extends EntityDef {
	public String quote;
	public String author;

	public ChapQuoteDef(BookishParser.AttrsContext attrsCtx) {
		super(0, attrsCtx.attributes);
		this.quote = attributes.get("quote");
		this.author = attributes.get("author");
	}
}
