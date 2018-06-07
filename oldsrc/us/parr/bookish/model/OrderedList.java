package us.parr.bookish.model;

import java.util.List;

public class OrderedList extends OutputModelObject {
	@ModelElement
	public List<ListItem> items;

	public OrderedList(List<ListItem> items) {
		this.items = items;
	}
}

