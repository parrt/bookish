package us.parr.bookish.model;

import java.util.Map;

public class Image extends OutputModelObject {
	public Map<String, String> attrs;

	public Image(Map<String, String> attrs) {
		this.attrs = attrs;
	}
}
