package us.parr.bookish.model;

import us.parr.bookish.entity.CitationDef;

public class Citation extends OutputModelObject {
	public CitationDef def;

	public Citation(CitationDef def) {
		this.def = def;
	}
}
