package us.parr.bookish.translate;

import org.stringtemplate.v4.StringRenderer;

import java.util.Locale;

public class LatexEscaper extends StringRenderer {
	@Override
	public String toString(Object o, String formatString, Locale locale) {
		String s = (String)o;
		if ( formatString!=null && formatString.equals("url-escape") ) {
			s = s.replace("~", "\\textasciitilde{}");
			s = escape(s);
			return s;
		}
		else if ( formatString!=null && formatString.equals("escape") ) {
			s = escape(s);
			return s;
		}
		return super.toString(o, formatString, locale);
	}

	private String escape(String s) {
		s = s.replace("\\", "\\\\");
		s = s.replace("_", "\\_");
		s = s.replace("#", "\\#");
		s = s.replace("&", "\\&");
		s = s.replace("$", "\\$");
		s = s.replace("%", "\\%");
		return s;
	}
}
