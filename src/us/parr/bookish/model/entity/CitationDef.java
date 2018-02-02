package us.parr.bookish.model.entity;

public class CitationDef extends EntityDef {
	public String title;
	public String bibinfo;

	public CitationDef(String label, String title, String bibinfo) {
		super(label);
		this.title = stripCurlies(title);
		this.bibinfo = stripCurlies(bibinfo);
	}
}
