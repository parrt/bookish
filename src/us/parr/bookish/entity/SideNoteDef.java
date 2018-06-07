package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class SideNoteDef extends EntityDef {
	public String note;

	public SideNoteDef(int index, BookishParser.AttrsContext attrsCtx, String note) {
		super(index, attrsCtx.attributes);
		this.note = note;
	}

	public boolean isSideItem() { return true; }
}
