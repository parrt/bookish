package us.parr.bookish.model;

import us.parr.bookish.translate.Translator;
import us.parr.lib.ParrtIO;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Image extends OutputModelObject {
	public static final Set<String> supportedSingleFormats = new HashSet<>() {{
		add("jpg");
		add("png");
	}};

	public Translator translator;
	public Map<String, String> attributes;

	public Image(Translator translator, Map<String, String> attributes) {
		this.translator = translator;
		this.attributes = attributes;
		if ( attributes.containsKey("src") ) {
			String src = attributes.get("src");
			String ext = ParrtIO.fileExtension(src);
			if ( ext!=null ) {
				if ( !supportedSingleFormats.contains(ext) ) {
					// then strip it and add appropriate ext for translation target
					if ( translator.tool.isHTMLTarget() ) {
						src = ParrtIO.replaceFileExtension(src, ".svg");
					}
					else if ( translator.tool.isLatexTarget() ) {
						src = ParrtIO.replaceFileExtension(src, ".pdf");
					}
				}
			}
			else {
				if ( translator.tool.isHTMLTarget() ) {
					src = src + ".svg";
				}
				else if ( translator.tool.isLatexTarget() ) {
					src = src + ".pdf";
				}
			}
			attributes.put("src", src);
		}
		else {
			System.err.println("No src attribute on IMG tag");
		}
		String width = attributes.get("width");
		width = translator.processImageWidth(width);
		attributes.put("width", width);
	}
}
