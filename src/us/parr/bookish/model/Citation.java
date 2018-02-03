package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

public class Citation extends OutputModelObject {
	@ModelElement
	public Block title;

	@ModelElement
	public Block bibinfo;

	public String label;
	public EntityDef def;

	public Citation(EntityDef def, String label, Block title, Block bibinfo) {
		this.def = def;
		this.title = title;
		this.bibinfo = bibinfo;
		this.label = label;
	}
}
