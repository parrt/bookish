package us.parr.bookish.model;

public class SideQuote extends OutputModelObject {
	@ModelElement
	public Block quote;

	@ModelElement
	public Block author;

	public String label;

	public SideQuote(String label, Block quote, Block author) {
		this.label = label;
		this.quote = quote;
		this.author = author;
	}
}
