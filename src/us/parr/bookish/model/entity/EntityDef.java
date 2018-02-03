package us.parr.bookish.model.entity;

import org.stringtemplate.v4.ST;
import us.parr.bookish.model.OutputModelObject;

import static us.parr.lib.ParrtStrings.stripQuotes;

public class EntityDef {
	public String label; // optional label
	public int index;    // if label!=null; indexed from 1

	public OutputModelObject model;
	public ST template;

	public EntityDef(int index, String label) {
		this.index = index;
		this.label = stripCurlies(label);
	}

	public static String stripCurlies(String s) {
		if ( s!=null && (s.startsWith("{") || s.startsWith("[")) ) {
			return stripQuotes(s);
		}
		return s;
	}
}

