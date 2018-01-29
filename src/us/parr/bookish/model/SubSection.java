package us.parr.bookish.model;

import java.util.List;

public class SubSection extends Section {
	public SubSection(String title,
	                  String anchor,
	                  List<OutputModelObject> elements,
	                  List<ContainerWithTitle> subsections)
	{
		super(title, anchor, elements, subsections);
	}

	@Override
	public String getAnchor() {
		if ( anchor!=null ) return anchor;
		return "sec"+parent.sectionNumber+"."+sectionNumber;
	}
}
