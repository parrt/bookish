package us.parr.bookish.translate;

import org.stringtemplate.v4.StringRenderer;
import us.parr.lib.ParrtCollections;
import us.parr.lib.ParrtStrings;

import java.util.Locale;
import java.util.regex.Pattern;

public class HTMLEscaper extends StringRenderer {
	@Override
	public String toString(Object o, String formatString, Locale locale) {
		String s = (String)o;
		if ( formatString!=null ) {
			switch ( formatString ) {
				case "xml-encode":
					s = escape(s);
					s = highlight(s);
					break;
				case "highlight" :
					s = highlight(s);
					break;
			}
			return s;
		}
		return super.toString(o, null, locale);
	}

	private String highlight(String s) {
		// lines beginning with '!' are to be highlighted so
		// dehighlight all the others.
		s = ParrtStrings.expandTabs(s, 4);
		Pattern c = Pattern.compile("^!", Pattern.MULTILINE);
		if ( c.matcher(s).find() ) { // any line starts with '!'?
			String[] lines = s.split("\\n");
			for (int i = 0; i<lines.length; i++) {
				if ( lines[i].startsWith("!") ) {
					lines[i] = "<span class=\"highlight\">" + lines[i].substring(1) + "</span>";
				}
			}
			s = ParrtCollections.join(lines, "\n");
		}
		return s;
	}

	private String escape(String s) {
		s = s.replace("<", "&lt;");
		return s;
	}

	public static void main(String[] args) {
		System.out.println(Pattern.compile("^!", Pattern.MULTILINE).matcher("hi\n!mom\nnews").find());
	}
}
