package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;
import us.parr.bookish.translate.Translator;

public class SideNoteDef extends EntityDef {
	public String note;

	public SideNoteDef(int index, Token startToken, String note) {
		super(index, startToken);
		this.note = Translator.stripCurlies(note);
	}
}
