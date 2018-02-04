package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;
import us.parr.bookish.translate.Translator;

public class CitationDef extends EntityDef {
	public String title;

	public String bibinfo;

	public CitationDef(int index, Token startToken, String title, String bibinfo) {
		super(index,startToken);
		this.title = Translator.stripCurlies(title);
		this.bibinfo = Translator.stripCurlies(bibinfo);
	}

	public boolean isGloballyVisible() { return true; }
}
