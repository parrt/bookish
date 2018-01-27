package us.parr.bookish.translate;

import org.stringtemplate.v4.StringRenderer;

import java.util.Locale;

public class LatexEscaper extends StringRenderer {
	@Override
	public String toString(Object o, String formatString, Locale locale) {
		if ( formatString!=null && formatString.equals("tex-encode") ) {
			String s = (String)o;
			s = s.replace("_", "\\_");
			s = s.replace("#", "\\#");
			s = s.replace("&", "\\&");
			return s;
		}
		return super.toString(o, formatString, locale);
	}
}
