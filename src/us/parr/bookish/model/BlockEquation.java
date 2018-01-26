package us.parr.bookish.model;

public class BlockEquation extends BlockImage {
	public String eqn;

	public BlockEquation(String imageFilename, String eqn) {
		super(imageFilename, "");
		this.eqn = eqn;
	}
}
