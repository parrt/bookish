package us.parr.bookish.model.entity;

public class ChapQuoteDef extends EntityDef {
	public String quote;
	public String author;

	public ChapQuoteDef(String quote, String author) {
		super(null);
		this.quote = stripCurlies(quote);
		this.author = stripCurlies(author);
	}
}
