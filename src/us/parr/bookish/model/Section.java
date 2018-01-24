package us.parr.bookish.model;

import java.util.List;

public class Section extends Chapter {
	public Section(String title, List<OutputModelObject> elements, List<? extends Section> sections) {
		super(title, elements, sections);
	}
}
