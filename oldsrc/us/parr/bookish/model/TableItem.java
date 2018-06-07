package us.parr.bookish.model;

import java.util.List;

public class TableItem extends OutputModelObject {
	@ModelElement
	public List<OutputModelObject> contents;

	public TableItem(List<OutputModelObject> contents) {
		this.contents = contents;
	}
}
