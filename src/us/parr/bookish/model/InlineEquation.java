package us.parr.bookish.model;

public class InlineEquation extends InlineImage {
	public String eqn;
	public float height, depth;
	public int descentPercentage;
	public int depthRounded;
	public float depthTweaked;

	public InlineEquation(String imageFilename, String eqn, float height, float depth) {
		super(imageFilename);
		this.eqn = eqn;
		this.height = height;
		this.depth = depth;
		if ( depth < 1.3 ) {
			depthTweaked = 0.5f;
		}
		else {
			depthTweaked = depth*1.20f;
		}
		this.descentPercentage = (int)(100*depth/(height+depth));
		depthRounded = Math.round(depth);
	}
}
