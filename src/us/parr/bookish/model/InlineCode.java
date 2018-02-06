package us.parr.bookish.model;

import java.util.List;

public class InlineCode extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public InlineCode(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
