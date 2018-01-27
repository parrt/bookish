package us.parr.bookish.model;

import java.util.Arrays;
import java.util.List;

public class Join extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public Join(List<OutputModelObject> elements) {
		this.elements = elements;
	}
	public Join(List<OutputModelObject> elements,
	            List<OutputModelObject> others)
	{
		this.elements = elements;
		this.elements.addAll(others);
	}

	public Join(OutputModelObject... elements) {
		this.elements = Arrays.asList(elements);
	}
}
