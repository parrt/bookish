package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

public class Citation extends OutputModelObject {
	@ModelElement
	public TextBlock title;

	@ModelElement
	public TextBlock bibinfo;

	public String label;
	public EntityDef def;

	public Citation(EntityDef def, String label, TextBlock title, TextBlock bibinfo) {
		this.def = def;
		this.title = title;
		this.bibinfo = bibinfo;
		this.label = label;
	}
}
