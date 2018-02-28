package us.parr.bookish.model;

public class Callout extends OutputModelObject {
	@ModelElement
	public TextBlock text;

	public Callout(TextBlock text) {
		this.text = text;
	}
}
