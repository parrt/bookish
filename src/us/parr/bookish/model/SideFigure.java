package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

import java.util.Map;

public class SideFigure extends OutputModelObject {
	@ModelElement
	public TextBlock code;

	public String caption;

	public String label;
	public EntityDef def;

	public Map<String,String> argMap;

	public SideFigure(EntityDef def, String label, TextBlock code, Map<String,String> argMap) {
		this.def = def;
		this.label = label;
		this.code = code;
		this.argMap = argMap;
		this.caption = argMap.get("caption");
	}
}
