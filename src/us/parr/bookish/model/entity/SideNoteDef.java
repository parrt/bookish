package us.parr.bookish.model.entity;

public class SideNoteDef extends EntityDef {
	public String note;

	public SideNoteDef(String label, String note) {
		super(label);
		this.note = note;
	}
}
