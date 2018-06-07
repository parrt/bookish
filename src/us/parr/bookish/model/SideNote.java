package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

public class SideNote extends OutputModelObject {
	@ModelElement
	public OutputModelObject text;

	public EntityDef def;

	public SideNote(EntityDef def, OutputModelObject text) {
		this.text = text;
		this.def = def;
	}
}
