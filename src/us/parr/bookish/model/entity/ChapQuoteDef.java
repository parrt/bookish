package us.parr.bookish.model.entity;

public class ChapQuoteDef extends EntityDef {
	public String quote;
	public String author;

	public ChapQuoteDef(String label, String quote, String author) {
		super(label);
		this.quote = quote;
		this.author = author;
	}
}
