package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.List;

public class SubSection extends Section {
	public SubSection(EntityDef def,
	                  String title,
	                  OutputModelObject content,
	                  List<ContainerWithTitle> subsections)
	{
		super(def, title, content, subsections);
	}
}
