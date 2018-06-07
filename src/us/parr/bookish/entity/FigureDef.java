package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class FigureDef extends EntityDef {
	public String caption;

	public FigureDef(int index, BookishParser.AttrsContext attrsCtx) {
		super(index, attrsCtx.attributes);
		this.caption = attributes.get("caption");
	}

	public boolean isGloballyVisible() { return true; }
}
