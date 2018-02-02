package us.parr.bookish.model.entity;

import us.parr.bookish.model.OutputModelObject;

import static us.parr.lib.ParrtStrings.stripQuotes;

public class EntityDef extends OutputModelObject {
	public String label; // optional label

	public EntityDef(String label) {
		this.label = stripCurlies(label);
	}

	public static String stripCurlies(String s) {
		if ( s!=null && (s.startsWith("{") || s.startsWith("[")) ) {
			return stripQuotes(s);
		}
		return s;
	}
}

