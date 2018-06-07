package us.parr.bookish.model;

import java.util.Map;

public class Aside extends OutputModelObject {
	@ModelElement
	public TextBlock text;

	public Map<String,String> attributes;

	public Aside(TextBlock text, Map<String,String> attributes) {
		this.text = text;
		this.attrs = attrs;
	}
}

