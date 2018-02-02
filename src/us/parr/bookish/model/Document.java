package us.parr.bookish.model;

public class Document extends OutputModelObject {
	@ModelElement
	public Chapter chapter;
	public String generatedFilename;

	public Document(Chapter chapter) {
		this.chapter = chapter;
	}
}
