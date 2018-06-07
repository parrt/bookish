package us.parr.bookish.entity;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;

import static us.parr.bookish.translate.Translator.splitSectionTitle;

public class SectionDef extends EntityWithScope {
	public String title;

	public SectionDef(int secNumber, Token titleToken, EntityWithScope enclosingScope) {
		super(secNumber, titleToken, enclosingScope);
		String title = titleToken.getText();
		title = title.substring(title.indexOf(' ')+1).trim();
		Pair<String, String> results = splitSectionTitle(title);
		this.title = results.a;
		this.label = results.b;
		if ( label==null ) {
			label = "sec:"+getContainerNumber();
		}
	}

	public boolean isGloballyVisible() { return true; }
}
