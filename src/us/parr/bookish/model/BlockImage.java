package us.parr.bookish.model;

import java.util.HashMap;
import java.util.Map;

public class BlockImage extends OutputModelObject {
	public String src;
	public String alt;
	public Map<String, String> attrs;

	public BlockImage(String src, String alt) {
		attrs = new HashMap<>();
		attrs.put("src", src);
		attrs.put("alt", alt);
	}

	public BlockImage(Map<String, String> attrs) {
//		if ( attrs.containsKey("src") ) {
//			src = attrs.get("src");
//		}
//		if ( attrs.containsKey("alt") ) {
//			alt = attrs.get("alt");
//		}
		this.attrs = attrs;
	}
}
