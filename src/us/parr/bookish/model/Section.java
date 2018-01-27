package us.parr.bookish.model;

import java.util.List;

public class Section extends Chapter {
	public Chapter parent; // who contains us? we inherit from Chapter also as elements are similar

	public Section(String title,
	               String anchor,
	               List<OutputModelObject> elements,
	               List<? extends Section> subsections)
	{
		super(title, anchor, null, null, elements, subsections);
	}

	public void connectSectionTree(Chapter parent, int n) {
		this.parent = parent;
		sectionNumber = n;
		int i = 1;
		if ( sections!=null ) {
			for (Section subsec : sections) {
				subsec.connectSectionTree(this, i++);
			}
		}
	}

	public String getAnchor() {
		if ( anchor!=null ) return anchor;
		return "sec"+sectionNumber;
	}
}
