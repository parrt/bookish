package us.parr.bookish.model;

import java.util.List;

public class Paragraph extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public Paragraph(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
