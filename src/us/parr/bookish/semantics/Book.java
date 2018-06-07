package us.parr.bookish.semantics;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.Tool;
import us.parr.bookish.translate.HTMLEscaper;
import us.parr.bookish.translate.LatexEscaper;

public class Book extends Artifact {
	public Book(Tool tool, String title) {
		super(tool, title);
	}

	public STGroup loadTemplates() {
		String templateFileName = "templates/"+tool.target+"-book.stg";
		STGroup templates = new STGroupFile(templateFileName);
		if ( tool.isLatexTarget() ) {
			templates.registerRenderer(String.class, new LatexEscaper());
		}
		else {
			templates.registerRenderer(String.class, new HTMLEscaper());
		}
		return templates;
	}
}