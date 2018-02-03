package us.parr.bookish.model.entity;

public class SiteDef extends EntityDef {
	public String website;

	public SiteDef(int index, String label, String website) {
		super(index, label);
		this.website = stripCurlies(website);
	}
}
