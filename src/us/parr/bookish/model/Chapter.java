package us.parr.bookish.model;

import java.util.List;

public class Chapter extends OutputModelObject {
	@ModelElement
	public Author author;
	@ModelElement
	public Abstract abstract_;
	@ModelElement
	public List<OutputModelObject> elements;
	@ModelElement
	public List<? extends Section> sections;

	public String title;
	public String anchor;

	public int sectionNumber;

	public Chapter(String title,
	               String anchor,
	               Author author,
	               Abstract abstract_,
	               List<OutputModelObject> elements,
	               List<? extends Section> sections)
	{
		this.author = author;
		this.abstract_ = abstract_;
		this.elements = elements;
		this.sections = sections;
		this.title = title;
		this.anchor = anchor;
	}

	public void connectSectionTree() {
		int i = 1;
		if ( sections!=null ) {
			for (Section sec : sections) {
				sec.connectSectionTree(this,i);
				i++;
			}
		}
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
