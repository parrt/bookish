package us.parr.bookish.model;

import java.util.List;

public class Quoted extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public Quoted(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
