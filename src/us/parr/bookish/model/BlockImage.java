package us.parr.bookish.model;

import java.util.HashMap;
import java.util.Map;

public class BlockImage extends Image {
	public BlockImage(String src, String alt) {
		super(
			new HashMap<String,String>()
				{{
					put("src", src);
					put("alt", alt);
				}}
		     );
	}

	public BlockImage(Map<String, String> attrs) {
		super(attrs);
	}
}
