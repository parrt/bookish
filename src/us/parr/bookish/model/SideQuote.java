package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

public class SideQuote extends OutputModelObject {
	@ModelElement
	public TextBlock quote;

	@ModelElement
	public TextBlock author;

	public String label;

	public EntityDef def;

	public SideQuote(EntityDef def, String label, TextBlock quote, TextBlock author) {
		this.def = def;
		this.label = label;
		this.quote = quote;
		this.author = author;
	}
}
