package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class SideFigDef extends FigureDef {
	public SideFigDef(int index, BookishParser.AttrsContext attrsCtx) {
		super(index, attrsCtx);
	}

	public boolean isSideItem() { return true; }
}
