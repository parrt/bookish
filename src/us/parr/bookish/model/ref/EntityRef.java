package us.parr.bookish.model.ref;

import us.parr.bookish.entity.EntityDef;
import us.parr.bookish.model.OutputModelObject;

public class EntityRef extends OutputModelObject {
	public EntityDef def;

	public EntityRef(EntityDef def) {
		this.def = def;
	}
}
