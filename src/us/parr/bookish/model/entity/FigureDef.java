package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;
import us.parr.bookish.translate.Translator;

public class FigureDef extends EntityDef {
	public String figure;
	public String caption;

	public FigureDef(int index, Token startToken, String figure, String caption) {
		super(index, startToken);
		this.figure = Translator.stripCurlies(figure);
		this.caption = Translator.stripCurlies(caption);
	}

	public boolean isGloballyVisible() { return true; }
}
