package us.parr.bookish.translate;

import org.antlr.v4.runtime.tree.ParseTree;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.model.Chapter;
import us.parr.bookish.model.Document;
import us.parr.bookish.model.Other;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.model.Paragraph;
import us.parr.bookish.model.Section;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.BookishParserBaseVisitor;

import java.util.ArrayList;
import java.util.List;

public class Translator extends BookishParserBaseVisitor<OutputModelObject> {
	public STGroupFile templates = new STGroupFile("templates/html.stg", '$', '$');

//	public BookishParser.DocumentContext doc;

//	public Translator(BookishParser.DocumentContext doc) {
//		this.doc = doc;
//	}

	@Override
	public OutputModelObject visitDocument(BookishParser.DocumentContext ctx) {
		List<OutputModelObject> elements = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			elements.add( visit(el) );
		}
		return new Document(elements);
	}

	@Override
	public OutputModelObject visitChapter(BookishParser.ChapterContext ctx) {
		String title = ctx.getText();
		title = title.substring(1).trim();
		return new Chapter(title);
	}

	@Override
	public OutputModelObject visitSection(BookishParser.SectionContext ctx) {
		String title = ctx.getText();
		title = title.substring(2).trim();
		return new Section(title);
	}

	@Override
	public OutputModelObject visitParagraph(BookishParser.ParagraphContext ctx) {
		List<OutputModelObject> elements = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			elements.add( visit(el) );
		}
		return new Paragraph(elements);
	}

	@Override
	public OutputModelObject visitOther(BookishParser.OtherContext ctx) {
		return new Other(ctx.getText());
	}
}
