package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

public class SideQuote extends OutputModelObject {
	@ModelElement
	public Block quote;

	@ModelElement
	public Block author;

	public String label;

	public EntityDef def;

	public SideQuote(EntityDef def, String label, Block quote, Block author) {
		this.def = def;
		this.label = label;
		this.quote = quote;
		this.author = author;
	}
}
