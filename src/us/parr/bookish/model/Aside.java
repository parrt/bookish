package us.parr.bookish.model;

import java.util.Map;

public class Aside extends OutputModelObject {
	@ModelElement
	public TextBlock text;

	public Map<String,String> attrs;

	public Aside(TextBlock text, Map<String,String> attrs) {
		this.text = text;
		this.attrs = attrs;
	}
}

