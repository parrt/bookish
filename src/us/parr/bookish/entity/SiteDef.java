package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class SiteDef extends EntityDef {
	public String website;

	public SiteDef(int index, BookishParser.AttrsContext attrsCtx) {
		super(index, attrsCtx.attributes);
		this.website = attrsCtx.attributes.get("url");
	}

	public boolean isGloballyVisible() { return true; }

	public boolean isSideItem() { return true; }
}
