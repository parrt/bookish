package us.parr.bookish.model;

import java.util.List;

public class Document extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public Document(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
