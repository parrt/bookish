package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

public class SubSubSection extends SubSection {
	public SubSubSection(EntityDef def,
	                     String title,
	                     String anchor,
	                     OutputModelObject content)
	{
		super(def, title, anchor, content, null);
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
