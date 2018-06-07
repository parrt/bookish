package us.parr.bookish.translate;

import org.stringtemplate.v4.StringRenderer;

import java.util.Locale;

public class HTMLEscaper extends StringRenderer {
	@Override
	public String toString(Object o, String formatString, Locale locale) {
		String s = (String)o;
		if ( formatString!=null ) {
			s = escape(s);
			return s;
		}
		return super.toString(o, null, locale);
	}

	private String escape(String s) {
		s = s.replace("<", "&lt;");
		return s;
	}

}
