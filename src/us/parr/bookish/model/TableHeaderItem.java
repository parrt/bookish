package us.parr.bookish.model;

import java.util.List;
import java.util.Map;

public class TableHeaderItem extends TableRowItem {
	public Integer widthPercentage = null;

	public TableHeaderItem(List<OutputModelObject> contents,
	                       Map<String, String> attributes)
	{
		super(contents, attributes);
		if ( attributes.containsKey("width") ) {
			String value = attributes.get("width");
			if ( value.endsWith("%") ) {
				widthPercentage = Integer.parseInt(value.substring(0,value.length()-1));
			}
		}
	}
}
