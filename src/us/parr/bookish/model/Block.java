package us.parr.bookish.model;

import java.util.List;

public class Block extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public Block(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
