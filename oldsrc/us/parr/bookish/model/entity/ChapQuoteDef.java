package us.parr.bookish.entity;

import us.parr.bookish.translate.Translator;

public class ChapQuoteDef extends EntityDef {
	public String quote;
	public String author;

	public ChapQuoteDef(String quote, String author) {
		super(0,null);
		this.quote = Translator.stripCurlies(quote);
		this.author = Translator.stripCurlies(author);
	}
}
