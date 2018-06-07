package us.parr.bookish.model;

import us.parr.bookish.entity.EntityDef;

import java.util.List;

/** Unused */
public class Article extends Chapter {
	public Article(EntityDef def,
	               String title,
	               String anchor,
	               Author author,
	               PreAbstract preabstract,
	               Abstract abstract_,
	               List<OutputModelObject> elements,
	               List<ContainerWithTitle> sections)
	{
		super(def, title, anchor, author, preabstract, abstract_, elements, sections);
	}
}
