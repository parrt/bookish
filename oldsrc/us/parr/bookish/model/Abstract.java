package us.parr.bookish.model;

import java.util.List;

public class Abstract extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public Abstract(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
