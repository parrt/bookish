package us.parr.bookish.model;

import us.parr.bookish.translate.Translator;
import us.parr.lib.ParrtIO;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Image extends OutputModelObject {
	public static final Set<String> supportedSingleFormats = new HashSet<String>() {{
		add("jpg");
		add("png");
	}};

	public Translator translator;
	public Map<String, String> attrs;

	public Image(Translator translator, Map<String, String> attrs) {
		this.translator = translator;
		this.attrs = attrs;
		if ( attrs.containsKey("src") ) {
			String src = attrs.get("src");
			String ext = ParrtIO.fileExtension(src);
			if ( ext!=null ) {
				if ( !supportedSingleFormats.contains(ext) ) {
					// then strip it and add appropriate ext for translation target
					if ( translator.isHTMLTarget() ) {
						src = ParrtIO.replaceFileExtension(src, ".svg");
					}
					else if ( translator.isLatexTarget() ) {
						src = ParrtIO.replaceFileExtension(src, ".pdf");
					}
				}
			}
			else {
				if ( translator.isHTMLTarget() ) {
					src = src + ".svg";
				}
				else if ( translator.isLatexTarget() ) {
					src = src + ".pdf";
				}
			}
			attrs.put("src", src);
		}
		else {
			System.err.println("No src attribute on IMG tag");
		}
		String width = attrs.get("width");
		width = translator.processImageWidth(width);
		attrs.put("width", width);
	}
}
