package us.parr.bookish.model;

public class Citation extends OutputModelObject {
	@ModelElement
	public Block title;

	@ModelElement
	public Block bibinfo;

	public String label;

	public Citation(String label, Block title, Block bibinfo) {
		this.title = title;
		this.bibinfo = bibinfo;
		this.label = label;
	}
}
