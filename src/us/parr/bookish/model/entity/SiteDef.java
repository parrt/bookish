package us.parr.bookish.model.entity;

public class SiteDef extends EntityDef {
	public String website;

	public SiteDef(String label, String website) {
		super(label);
		this.website = stripCurlies(website);
	}
}
