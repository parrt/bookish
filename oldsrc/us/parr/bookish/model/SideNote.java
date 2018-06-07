package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

public class SideNote extends OutputModelObject {
	@ModelElement
	public TextBlock text;

	public String label;

	public EntityDef def;

	public SideNote(EntityDef def, String label, TextBlock text) {
		this.text = text;
		this.label = label;
		this.def = def;
	}
}
