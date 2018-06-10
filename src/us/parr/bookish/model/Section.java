package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.List;

public class Section extends ContainerWithTitle {
	public Section(EntityDef def,
	               String title,
	               OutputModelObject content,
	               List<ContainerWithTitle> subsections)
	{
		super(def, title, content);
		this.subcontainers = subsections;
	}

	public String getAnchor() {
		return def.label;
	}
}
