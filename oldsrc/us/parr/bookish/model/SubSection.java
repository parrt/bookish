package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.List;

public class SubSection extends Section {
	public SubSection(EntityDef def,
	                  String title,
	                  String anchor,
	                  List<OutputModelObject> elements,
	                  List<ContainerWithTitle> subsections)
	{
		super(def, title, anchor, elements, subsections);
	}

	@Override
	public String getAnchor() {
		if ( anchor!=null ) return anchor;
		return "sec"+parent.sectionNumber+"."+sectionNumber;
	}
}
