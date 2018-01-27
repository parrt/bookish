package us.parr.bookish.model;

public class Latex extends BlockImage {
	public String text;

	public Latex(String src, String alt, String text) {
		super(src, alt);
		this.text = text;
	}
}
