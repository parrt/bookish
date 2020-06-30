package us.parr.bookish.model;

import java.util.Map;

public class YouTube extends OutputModelObject {
	public String src;
	public String width;
	public String height;

	public YouTube(Map<String, String> attributes) {
		this.src = attributes.get("src");
		this.width = attributes.get("width");
		this.height = attributes.get("height");
	}
}
