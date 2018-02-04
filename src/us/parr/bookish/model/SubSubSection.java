package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

import java.util.List;

public class SubSubSection extends SubSection {
	public SubSubSection(EntityDef def,
	                     String title,
	                     String anchor,
	                     List<OutputModelObject> elements)
	{
		super(def, title, anchor, elements, null);
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
