package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

public class SideFigure extends OutputModelObject {
	@ModelElement
	public TextBlock code;

	@ModelElement
	public TextBlock caption;

	public String label;
	public EntityDef def;

	public SideFigure(EntityDef def, String label, TextBlock code, TextBlock caption) {
		this.def = def;
		this.label = label;
		this.code = code;
		this.caption = caption;
	}
}
