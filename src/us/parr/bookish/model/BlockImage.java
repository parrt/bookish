package us.parr.bookish.model;

import us.parr.bookish.translate.Translator;

import java.util.HashMap;
import java.util.Map;

public class BlockImage extends Image {
	public BlockImage(Translator translator, String src, String alt) {
		super(
			translator,
			new HashMap<>()
				{{
					put("src", src);
					put("alt", alt);
				}}
		     );
	}

	public BlockImage(Translator translator, Map<String, String> attrs) {
		super(translator, attrs);
	}
}
