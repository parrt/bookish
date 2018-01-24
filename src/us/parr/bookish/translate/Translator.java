package us.parr.bookish.translate;

import org.antlr.v4.runtime.tree.ParseTree;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.model.BlockImage;
import us.parr.bookish.model.Chapter;
import us.parr.bookish.model.Document;
import us.parr.bookish.model.HyperLink;
import us.parr.bookish.model.InlineImage;
import us.parr.bookish.model.Join;
import us.parr.bookish.model.Other;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.model.Paragraph;
import us.parr.bookish.model.Section;
import us.parr.bookish.model.SubSection;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.BookishParserBaseVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static us.parr.bookish.Tool.outputDir;

public class Translator extends BookishParserBaseVisitor<OutputModelObject> {
	public STGroupFile templates = new STGroupFile("templates/HTML.stg", '$', '$');

	public int eqnCounter = 1;

	@Override
	protected OutputModelObject aggregateResult(OutputModelObject aggregate, OutputModelObject nextResult) {
		if ( aggregate == null ) {
			return nextResult;
		}
		if ( nextResult == null ) {
			return aggregate;
		}
		if ( aggregate instanceof Join ) {
			List<OutputModelObject> elements = new ArrayList<>();
			elements.addAll(((Join) aggregate).elements);
			elements.add(nextResult);
			return new Join(elements);
		}
		return new Join(aggregate, nextResult);
	}

	@Override
	public OutputModelObject visitDocument(BookishParser.DocumentContext ctx) {
		return new Document((Chapter)visit(ctx.chapter()));
	}

	@Override
	public OutputModelObject visitChapter(BookishParser.ChapterContext ctx) {
		String title = ctx.chap.getText();
		title = title.substring(1).trim();

		List<OutputModelObject> elements = new ArrayList<>();
		List<Section> sections = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			OutputModelObject m = visit(el);
			if ( m instanceof Section ) {
				sections.add((Section)m);
			}
			else {
				elements.add(m);
			}
		}
		return new Chapter(title, elements, sections);
	}

	@Override
	public OutputModelObject visitSection(BookishParser.SectionContext ctx) {
		String title = ctx.sec.getText();
		title = title.substring(2).trim();

		List<OutputModelObject> elements = new ArrayList<>();
		List<SubSection> subsections = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			OutputModelObject m = visit(el);
			if ( m instanceof SubSection ) {
				subsections.add((SubSection)m);
			}
			else {
				elements.add(m);
			}
		}
		return new Section(title, elements, subsections);
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

	@Override
	public OutputModelObject visitBlock_eqn_content(BookishParser.Block_eqn_contentContext ctx) {
		String eqn = ctx.getText();
		String svg = Tex2SVGKt.tex2svg(eqn, false,14);
		String src = "n/a";
		try {
			src = outputDir+"/images/t"+eqnCounter+".svg";
			Files.write(Paths.get(src), svg.getBytes());
			eqnCounter++;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return new BlockImage(src);
//		return new BlockEquation(svg);
	}

	@Override
	public OutputModelObject visitEqn_content(BookishParser.Eqn_contentContext ctx) {
		String eqn = ctx.getText();
		String svg = Tex2SVGKt.tex2svg(eqn, false,14);
		String src = "n/a";
		try {
			src = outputDir+"/images/t"+eqnCounter+".svg";
			Files.write(Paths.get(src), svg.getBytes());
			eqnCounter++;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return new InlineImage(src);
//		return new InlineEquation(svg);
	}

	@Override
	public OutputModelObject visitBlock_eqn_element(BookishParser.Block_eqn_elementContext ctx) {
//		switch ( ctx.start.getType() ) {
//			case EQN_UNDERSCORE :
//			case EQN_NL :
//			case  EQN_OTHER :
//		}
		return new Other(ctx.start.getText());
	}

	@Override
	public OutputModelObject visitLink(BookishParser.LinkContext ctx) {
		String txt = ctx.getText();
		int middle = txt.indexOf("]("); // e.g., [name](link)
		String title = txt.substring(1,middle);
		String href = txt.substring(middle+2,txt.length()-1);
		return new HyperLink(title,href);
	}
}
