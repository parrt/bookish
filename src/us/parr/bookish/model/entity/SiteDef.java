package us.parr.bookish.model.entity;

import static us.parr.lib.ParrtStrings.stripQuotes;

public class SiteDef extends EntityDef {
	public String website;

	public SiteDef(String label, String website) {
		super(label);
		if ( website.startsWith("[") ) {
			website = stripQuotes(website);
		}
		this.website = website;
	}
}
