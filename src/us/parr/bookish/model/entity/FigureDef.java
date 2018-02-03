package us.parr.bookish.model.entity;

public class FigureDef extends EntityDef {
	public String figure;
	public String caption;

	public FigureDef(int index, String label, String figure, String caption) {
		super(index, label);
		this.figure = stripCurlies(figure);
		this.caption = stripCurlies(caption);
	}
}
