package us.parr.bookish.model;

public class HyperLink extends OutputModelObject {
	public String title;
	public String href;

	public HyperLink(String title, String href) {
		this.title = title;
		this.href = href;
	}
}
