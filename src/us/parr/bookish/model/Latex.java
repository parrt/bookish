package us.parr.bookish.model;

import us.parr.bookish.translate.Translator;

public class Latex extends BlockImage {
	public String text;

	public Latex(Translator translator, String src, String alt, String text) {
		super(translator, src, alt);
		this.text = text;
	}
}
