package us.parr.bookish.model;

import java.util.Map;

public class Aside extends OutputModelObject {
	@ModelElement
	public OutputModelObject text;

	public Map<String,String> attributes;

	public Aside(OutputModelObject text, Map<String,String> attributes) {
		this.text = text;
		this.attributes = attributes;
	}
}

