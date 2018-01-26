package us.parr.bookish.model;

import java.util.ArrayList;
import java.util.List;

public class ListItem extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public ListItem(List<OutputModelObject> elements) {
		this.elements = elements;
	}

	public ListItem(OutputModelObject element) {
		elements = new ArrayList<>();
		elements.add(element);
	}
}
