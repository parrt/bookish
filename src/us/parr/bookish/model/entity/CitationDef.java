package us.parr.bookish.model.entity;

public class CitationDef extends EntityDef {
	public String title;
	public String bibinfo;

	public CitationDef(int index, String label, String title, String bibinfo) {
		super(index, label);
		this.title = stripCurlies(title);
		this.bibinfo = stripCurlies(bibinfo);
	}
}
