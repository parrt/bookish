package us.parr.bookish.model.entity;

public class FigureDef extends EntityDef {
	public String figure;
	public String caption;

	public FigureDef(String label, String figure, String caption) {
		super(label);
		this.figure = figure;
		this.caption = caption;
	}
}
