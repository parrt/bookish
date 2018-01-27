package us.parr.bookish.model;

import java.util.List;

public class Author extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public Author(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
