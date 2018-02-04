package us.parr.bookish.model.entity;

import us.parr.bookish.translate.Translator;

public class SiteDef extends EntityDef {
	public String website;

	public SiteDef(int index, String label, String website) {
		super(index, label);
		this.website = Translator.stripCurlies(website);
	}

	public boolean isGloballyVisible() { return true; }
}
