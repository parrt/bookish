package us.parr.bookish.semantics;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.Tool;
import us.parr.bookish.translate.LatexEscaper;

public class Article extends Artifact {
	public Article(Tool tool, String title) {
		super(tool, title);
	}

	public STGroup loadTemplates() {
		String templateFileName = "templates/"+tool.target+"-article.stg";
		STGroup templates = new STGroupFile(templateFileName);
		templates.registerRenderer(String.class, new LatexEscaper());
		return templates;
	}
}
