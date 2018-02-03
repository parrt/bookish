package us.parr.bookish.model;

public class ChapQuote extends OutputModelObject {
	@ModelElement
	public Block quote;

	@ModelElement
	public Block author;

	public ChapQuote(Block quote, Block author) {
		this.quote = quote;
		this.author = author;
	}
}
