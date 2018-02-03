package us.parr.bookish.model;

public class SideFigure extends OutputModelObject {
	@ModelElement
	public Block code;

	@ModelElement
	public Block caption;

	public String label;

	public SideFigure(String label, Block code, Block caption) {
		this.label = label;
		this.code = code;
		this.caption = caption;
	}
}
