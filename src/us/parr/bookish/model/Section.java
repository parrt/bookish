package us.parr.bookish.model;

import java.util.List;

public class Section extends ContainerWithTitle {
	public Section(String title,
	               String anchor,
	               List<OutputModelObject> elements,
	               List<ContainerWithTitle> subsections)
	{
		super(title, anchor, elements);
		this.subcontainers = subsections;
	}

	public void connectContainerTree(ContainerWithTitle parent, int n) {
		this.parent = parent;
		sectionNumber = n;
		int i = 1;
		if ( subcontainers!=null ) {
			for (ContainerWithTitle subcontainer : subcontainers) {
				subcontainer.connectContainerTree(this, i++);
			}
		}
	}

	public String getAnchor() {
		if ( anchor!=null ) return anchor;
		return "sec"+sectionNumber;
	}
}
