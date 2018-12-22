package us.parr.bookish.model.ref;

import us.parr.bookish.entity.EntityDef;
import us.parr.bookish.model.ModelElement;
import us.parr.bookish.model.OutputModelObject;

import java.util.Map;

public class Figure extends OutputModelObject {
	@ModelElement
	public OutputModelObject code;

	public String caption;

	public String label;
	public EntityDef def;

	public Map<String,String> attributes;

	public Figure(EntityDef def, String label, OutputModelObject code) {
		this.def = def;
		this.label = label;
		this.code = code;
		this.attributes = def.attributes;
		this.caption = attributes.get("caption");
	}
}
