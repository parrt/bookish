package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.misc.Pair;

import static us.parr.bookish.translate.Translator.splitSectionTitle;

public class ChapterDef extends EntityDef {
	public String title;

	public ChapterDef(String title) {
		super(0, null);
		title = title.substring(title.indexOf(' ')+1).trim();
		Pair<String, String> results = splitSectionTitle(title);
		this.title = results.a;
		this.label = results.b;
	}

	public boolean isGloballyVisible() { return true; }
}
