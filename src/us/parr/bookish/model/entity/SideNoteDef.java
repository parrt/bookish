package us.parr.bookish.model.entity;

import us.parr.bookish.translate.Translator;

public class SideNoteDef extends EntityDef {
	public String note;

	public SideNoteDef(int index, String label, String note) {
		super(index, label);
		this.note = Translator.stripCurlies(note);
	}
}
