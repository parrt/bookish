package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class SideQuoteDef extends EntityDef {
	public String quote;
	public String author;

	public SideQuoteDef(int index, BookishParser.AttrsContext attrsCtx) {
		super(index, attrsCtx.attributes);
		this.quote = attributes.get("quote");
		this.author = attributes.get("author");
	}

	public boolean isSideItem() { return true; }
}
