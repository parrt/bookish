package us.parr.bookish.model.entity;

import us.parr.bookish.translate.Translator;

public class CitationDef extends EntityDef {
	public String title;
	public String bibinfo;

	public CitationDef(int index, String label, String title, String bibinfo) {
		super(index,label);
		this.title = Translator.stripCurlies(title);
		this.bibinfo = Translator.stripCurlies(bibinfo);
	}

	public boolean isGloballyVisible() { return true; }
}
