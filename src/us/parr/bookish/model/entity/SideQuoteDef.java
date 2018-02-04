package us.parr.bookish.model.entity;

import us.parr.bookish.translate.Translator;

public class SideQuoteDef extends EntityDef {
	public String quote;
	public String author;

	public SideQuoteDef(int index, String label, String quote, String author) {
		super(index, label);
		this.quote = Translator.stripCurlies(quote);
		this.author = Translator.stripCurlies(author);
	}
}
