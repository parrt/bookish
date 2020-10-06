package us.parr.bookish.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableRowItem extends TableItem {
	public Map<String, String> attributes;

	public TableRowItem(List<OutputModelObject> contents,
	                    Map<String, String> attributes)
	{
		super(contents);
		if ( attributes==Collections.EMPTY_MAP ) {
			attributes = new HashMap<String, String>();
		}
		this.attributes = attributes;
		attributes.putIfAbsent("align", "center");
	}
}
