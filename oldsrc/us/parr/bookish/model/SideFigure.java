package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.Map;

public class SideFigure extends OutputModelObject {
	@ModelElement
	public TextBlock code;

	public String caption;

	public String label;
	public EntityDef def;

	public Map<String,String> attributes;

	public SideFigure(EntityDef def, String label, TextBlock code) {
		this.def = def;
		this.label = label;
		this.code = code;
		this.attributes = def.attributes;
		this.caption = attributes.get("caption");
	}
}
