package us.parr.bookish.model;

public class InlineEquation extends InlineImage {
	public String eqn;
	public float height, depth;
	public int descentPercentage;
	public int depthRounded;

	public InlineEquation(String imageFilename, String eqn, float height, float depth) {
		super(imageFilename);
		this.eqn = eqn;
		this.height = height;
		this.depth = depth;
		this.descentPercentage = (int)(100*depth/(height+depth));
		depthRounded = Math.round(depth);
	}
}
