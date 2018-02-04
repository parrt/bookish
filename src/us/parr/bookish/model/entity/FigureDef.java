package us.parr.bookish.model.entity;

import us.parr.bookish.translate.Translator;

public class FigureDef extends EntityDef {
	public int index;
	public String figure;
	public String caption;

	public FigureDef(int index, String label, String figure, String caption) {
		super(index, label);
		this.figure = Translator.stripCurlies(figure);
		this.caption = Translator.stripCurlies(caption);
	}

	public boolean isGloballyVisible() { return true; }
}
