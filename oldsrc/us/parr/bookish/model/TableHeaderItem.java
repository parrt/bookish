package us.parr.bookish.model;

import java.util.List;
import java.util.Map;

public class TableHeaderItem extends TableItem {
	public Integer widthPercentage = null;
	public Map<String, String> attrs;

	public TableHeaderItem(List<OutputModelObject> contents,
	                       Map<String, String> attrs)
	{
		super(contents);
		this.attrs = attrs;
		if ( attrs.containsKey("width") ) {
			String value = attrs.get("width");
			if ( value.endsWith("%") ) {
				widthPercentage = Integer.parseInt(value.substring(0,value.length()-1));
			}
		}
	}
}
