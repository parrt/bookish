package us.parr.bookish.model;

import java.util.List;

public class Chapter extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;
	@ModelElement
	public List<? extends Section> sections;

	public String title;

	public Chapter(String title, List<OutputModelObject> elements, List<? extends Section> sections) {
		this.title = title;
		this.elements = elements;
		this.sections = sections;
	}
}
