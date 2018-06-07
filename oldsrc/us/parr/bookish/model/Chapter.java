package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.List;

public class Chapter extends ContainerWithTitle {
	@ModelElement
	public Author author;
	@ModelElement
	public PreAbstract preabstract;
	@ModelElement
	public Abstract abstract_;

	public Chapter(EntityDef def,
	               String title,
	               String anchor,
	               Author author,
	               PreAbstract preabstract,
	               Abstract abstract_,
	               List<OutputModelObject> elements,
	               List<ContainerWithTitle> sections)
	{
		super(def, title, anchor, elements);
		this.author = author;
		this.abstract_ = abstract_;
		this.preabstract = preabstract;
		this.subcontainers = sections;
	}

	@Override
	public void connectContainerTree(ContainerWithTitle parent, int n) {
		int i = 1;
		if ( subcontainers!=null ) {
			for (ContainerWithTitle sec : subcontainers) {
				sec.connectContainerTree(this, i);
				i++;
			}
		}
	}

	public void connectContainerTree() {
		connectContainerTree(null,0);
	}

	/*
	public OutputModelObject createTOCModel() {
		if ( sections!=null ) {
			List<ListItem> seclist = new ArrayList<>();
			for (Section sec : sections) {
				System.out.println(sec.getAnchor());
				seclist.add(new ListItem(new Other(sec.getAnchor())));
				if ( sec.sections!=null ) {
					List<ListItem> subseclist = new ArrayList<>();
					for (Section subsec : sec.sections) {
						System.out.println("\t"+subsec.getAnchor());
						subseclist.add(new ListItem(new Other(subsec.getAnchor())));
					}
					seclist.add(new ListItem(new UnOrderedList(subseclist)));
				}
			}
			return new UnOrderedList(seclist);
		}
		return null;
	}
	*/
}
