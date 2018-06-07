package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class CitationDef extends EntityDef {
	public String title;
	public String author;

	public CitationDef(int index, BookishParser.AttrsContext attrsCtx) {
		super(index, attrsCtx.attributes);
		title = attrsCtx.attributes.get("title");
		author = attrsCtx.attributes.get("author");
	}

	public boolean isGloballyVisible() { return true; }
	public boolean isSideItem() { return true; }
}
