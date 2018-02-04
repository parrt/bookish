package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;

import static us.parr.bookish.translate.Translator.splitSectionTitle;

public class SectionDef extends EntityDef {
	public String title;

	public SectionDef(Token titleToken) {
		super(0, titleToken);
		String title = titleToken.getText();
		title = title.substring(title.indexOf(' ')+1).trim();
		Pair<String, String> results = splitSectionTitle(title);
		this.title = results.a;
		this.label = results.b;
	}

	public boolean isGloballyVisible() { return true; }
}
