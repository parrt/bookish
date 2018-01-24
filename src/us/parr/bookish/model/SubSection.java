package us.parr.bookish.model;

import java.util.List;

public class SubSection extends Section {
	public SubSection(String title, List<OutputModelObject> elements, List<Section> sections) {
		super(title, elements, sections);
	}
}
