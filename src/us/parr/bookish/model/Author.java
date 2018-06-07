package us.parr.bookish.model;

public class Author extends OutputModelObject {
	@ModelElement
	public OutputModelObject author;

	public Author(OutputModelObject author) {
		this.author = author;
	}
}
