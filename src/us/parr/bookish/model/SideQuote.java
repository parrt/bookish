package us.parr.bookish.model;

import us.parr.bookish.entity.SideQuoteDef;

import java.util.Map;

public class SideQuote extends OutputModelObject {
	public SideQuoteDef def;

	public Map<String,String> attributes;

	public SideQuote(SideQuoteDef def) {
		this.def = def;
		this.attributes = def.attributes;
	}
}
