package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.ArrayList;
import java.util.List;

public class Paragraph extends OutputModelObject {
	@ModelElement
	public OutputModelObject content;

	public List<EntityDef> entitiesRefd;
	public List<EntityDef> entitiesOnRight = new ArrayList<>();

	public Paragraph(OutputModelObject content, List<EntityDef> entitiesRefd) {
		this.content = content;
		this.entitiesRefd = entitiesRefd;
		for (EntityDef def : entitiesRefd) {
			if ( def.isSideItem() ) {
				entitiesOnRight.add(def);
			}
		}
	}
}
