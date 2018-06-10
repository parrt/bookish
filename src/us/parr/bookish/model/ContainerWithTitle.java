package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.List;

public abstract class ContainerWithTitle extends OutputModelObject {
	@ModelElement
	public OutputModelObject content;
	@ModelElement
	public List<ContainerWithTitle> subcontainers;

	public ContainerWithTitle parent; // who contains us?
	public String title;

	public EntityDef def;

	public int sectionNumber;

	public ContainerWithTitle(EntityDef def, String title, OutputModelObject content) {
		this.def = def;
		if ( def!=null ) {
			def.model = this;
		}
		this.content = content;
		this.title = title;
	}
}
