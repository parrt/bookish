package us.parr.bookish.model;

public class ChapQuote extends OutputModelObject {
	public String quote;
	public String author;

	public ChapQuote(String quote, String author) {
		this.quote = quote;
		this.author = author;
	}
}
