package us.parr.bookish.model;

import java.util.List;

public class SubSubSection extends SubSection {
	public SubSubSection(String title, String anchor, List<OutputModelObject> elements) {
		super(title, anchor, elements, null);
	}

	@Override
	public String getAnchor() {
		if ( anchor!=null ) return anchor;
		return "sec"+
			parent.parent.sectionNumber+"."+
			parent.sectionNumber+"."+
			sectionNumber;
	}
}
