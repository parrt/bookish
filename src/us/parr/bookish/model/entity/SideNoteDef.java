package us.parr.bookish.model.entity;

public class SideNoteDef extends EntityDef {
	public String note;

	public SideNoteDef(int index, String label, String note) {
		super(index, label);
		this.note = stripCurlies(note);
	}
}
