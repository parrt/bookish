package us.parr.bookish.model.entity;

import static us.parr.lib.ParrtStrings.stripQuotes;

public class EntityDef {
	public String label; // optional label

	public EntityDef(String label) {
		if ( label.startsWith("[") ) {
			label = stripQuotes(label);
		}
		this.label = label;
	}
}

