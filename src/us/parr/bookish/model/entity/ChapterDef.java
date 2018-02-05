package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;

import static us.parr.bookish.translate.Translator.splitSectionTitle;

public class ChapterDef extends EntityWithScope {
	public String title;

	public ChapterDef(int chapNumber, Token titleToken, EntityWithScope enclosingScope) {
		super(chapNumber, titleToken, enclosingScope);
		String title = titleToken.getText();
		title = title.substring(title.indexOf(' ')+1).trim();
		Pair<String, String> results = splitSectionTitle(title);
		this.title = results.a;
		this.label = results.b;
		if ( label==null ) {
			label = "chp:"+getContainerNumber();
		}
	}

	public boolean isGloballyVisible() { return true; }
}
