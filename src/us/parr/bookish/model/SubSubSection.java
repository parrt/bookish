package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

public class SubSubSection extends SubSection {
	public SubSubSection(EntityDef def,
	                     String title,
	                     OutputModelObject content)
	{
		super(def, title, content, null);
	}
}
