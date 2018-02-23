package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

import java.util.ArrayList;
import java.util.List;

public class Paragraph extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> elements;

	public List<EntityDef> entitiesRefd;
	public List<EntityDef> entitiesOnRight = new ArrayList<>();

	public Paragraph(List<OutputModelObject> elements, List<EntityDef> entitiesRefd) {
		this.elements = elements;
		this.entitiesRefd = entitiesRefd;
		for (EntityDef def : entitiesRefd) {
			if ( def.isSideItem() ) {
				entitiesOnRight.add(def);
			}
		}
	}
}
