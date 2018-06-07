package us.parr.bookish.model;

import java.util.List;

public class PreAbstract extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public PreAbstract(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
