package us.parr.bookish.model;

public class ChapQuote extends OutputModelObject {
	@ModelElement
	public TextBlock quote;

	@ModelElement
	public TextBlock author;

	public ChapQuote(TextBlock quote, TextBlock author) {
		this.quote = quote;
		this.author = author;
	}
}
