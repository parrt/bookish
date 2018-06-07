package us.parr.bookish.model;

public class Callout extends OutputModelObject {
	@ModelElement
	public OutputModelObject text;

	public Callout(OutputModelObject text) {
		this.text = text;
	}
}
