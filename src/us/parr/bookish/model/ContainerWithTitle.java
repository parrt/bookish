package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

import java.util.List;

public abstract class ContainerWithTitle extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;
	@ModelElement
	public List<ContainerWithTitle> subcontainers;

	public ContainerWithTitle parent; // who contains us?
	public String title;
	public String anchor;

	public EntityDef def;

	public int sectionNumber;

	public ContainerWithTitle(EntityDef def, String title, String anchor, List<OutputModelObject> elements) {
		this.def = def;
		if ( def!=null ) {
			def.model = this;
		}
		this.elements = elements;
		this.title = title;
		this.anchor = anchor;
	}

	public abstract void connectContainerTree(ContainerWithTitle parent, int n);
}
