package us.parr.bookish.model;

import java.util.Map;

public class XMLTag extends OutputModelObject {
	public String name;
	public Map<String, String> attrs;

	public XMLTag(String name, Map<String, String> attrs) {
		this.name = name;
		this.attrs = attrs;
	}
}
