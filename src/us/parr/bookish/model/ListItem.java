package us.parr.bookish.model;

import java.util.List;

public class ListItem extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public ListItem(List<OutputModelObject> elements) {
		this.elements = elements;
	}
}
