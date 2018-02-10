package us.parr.bookish.model;

import java.util.List;

/** A block of {...} text in curlies */
public class TextBlock extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public TextBlock(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
