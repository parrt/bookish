package us.parr.bookish.model;

import us.parr.bookish.translate.Translator;

public class BlockEquation extends BlockImage {
	public String eqn;

	public BlockEquation(Translator translator, String imageFilename, String eqn) {
		super(translator, imageFilename, "");
		this.eqn = eqn;
	}
}
