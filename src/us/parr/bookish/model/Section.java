package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.List;

public class Section extends ContainerWithTitle {
	public Section(EntityDef def,
	               String title,
	               String anchor,
	               OutputModelObject content,
	               List<ContainerWithTitle> subsections)
	{
		super(def, title, anchor, content);
		this.subcontainers = subsections;
	}

	public String getAnchor() {
		if ( anchor!=null ) return anchor;
		return "sec"+sectionNumber;
	}
}
