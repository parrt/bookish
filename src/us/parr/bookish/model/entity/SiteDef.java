package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;
import us.parr.bookish.translate.Translator;

public class SiteDef extends EntityDef {
	public String website;

	public SiteDef(int index, Token startToken, String website) {
		super(index, startToken);
		this.website = Translator.stripCurlies(website);
	}

	public boolean isGloballyVisible() { return true; }

	public boolean isSideItem() { return true; }
}
