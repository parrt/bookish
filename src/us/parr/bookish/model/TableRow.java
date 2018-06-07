package us.parr.bookish.model;

import java.util.List;

public class TableRow extends OutputModelObject {
	@ModelElement
	public List<TableItem> items;

	public TableRow(List<TableItem> items) {
		this.items = items;
	}
}
