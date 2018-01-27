package us.parr.bookish.model;

import java.util.List;

public abstract class ContainerWithTitle extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;
	@ModelElement
	public List<ContainerWithTitle> subcontainers;

	public ContainerWithTitle parent; // who contains us?
	public String title;
	public String anchor;

	public int sectionNumber;

	public ContainerWithTitle(String title, String anchor, List<OutputModelObject> elements) {
		this.elements = elements;
		this.title = title;
		this.anchor = anchor;
	}

	public abstract void connectContainerTree(ContainerWithTitle parent, int n);
}
