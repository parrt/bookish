package us.parr.bookish.model.entity;

public class SideQuoteDef extends EntityDef {
	public String quote;
	public String author;

	public SideQuoteDef(String label, String quote, String author) {
		super(label);
		this.quote = quote;
		this.author = author;
	}
}
