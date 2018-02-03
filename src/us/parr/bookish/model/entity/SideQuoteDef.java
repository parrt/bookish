package us.parr.bookish.model.entity;

public class SideQuoteDef extends EntityDef {
	public String quote;
	public String author;

	public SideQuoteDef(int index, String label, String quote, String author) {
		super(index, label);
		this.quote = stripCurlies(quote);
		this.author = stripCurlies(author);
	}
}
