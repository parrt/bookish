package us.parr.bookish.model;

public class InlineEquation extends InlineImage {
	public String eqn;

	public InlineEquation(String imageFilename, String eqn) {
		super(imageFilename);
		this.eqn = eqn;
	}
}
