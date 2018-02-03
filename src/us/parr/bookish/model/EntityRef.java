package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

public class EntityRef extends OutputModelObject {
	public EntityDef def;

	public EntityRef(EntityDef def) {
		this.def = def;
	}
}
