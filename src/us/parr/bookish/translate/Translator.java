package us.parr.bookish.translate;

import org.antlr.v4.runtime.tree.ParseTree;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.model.BlockImage;
import us.parr.bookish.model.Bold;
import us.parr.bookish.model.Chapter;
import us.parr.bookish.model.Document;
import us.parr.bookish.model.EqnIndexedVar;
import us.parr.bookish.model.EqnIndexedVecVar;
import us.parr.bookish.model.EqnVar;
import us.parr.bookish.model.EqnVecVar;
import us.parr.bookish.model.HyperLink;
import us.parr.bookish.model.InlineImage;
import us.parr.bookish.model.Italics;
import us.parr.bookish.model.Join;
import us.parr.bookish.model.ListItem;
import us.parr.bookish.model.OrderedList;
import us.parr.bookish.model.Other;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.model.Paragraph;
import us.parr.bookish.model.Section;
import us.parr.bookish.model.SubSection;
import us.parr.bookish.model.Table;
import us.parr.bookish.model.TableItem;
import us.parr.bookish.model.TableRow;
import us.parr.bookish.model.UnOrderedList;
import us.parr.bookish.model.XMLEndTag;
import us.parr.bookish.model.XMLTag;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.BookishParserBaseVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static us.parr.bookish.Tool.outputDir;
import static us.parr.bookish.parse.BookishParser.END_TAG;

public class Translator extends BookishParserBaseVisitor<OutputModelObject> {
	public static int INLINE_EQN_FONT_SIZE = 13;
	public static int BLOCK_EQN_FONT_SIZE = 13;
	public STGroupFile templates = new STGroupFile("templates/HTML.stg", '$', '$');

	public int eqnCounter = 1;
	public Pattern eqnVarPattern;
	public Pattern eqnVecVarPattern;
	public Pattern eqnIndexedVarPattern;
	public Pattern eqnIndexedVecVarPattern;

	public Translator() {
		eqnVarPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)");
		eqnIndexedVarPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)_([a-zA-Z][a-zA-Z0-9]*)");
		eqnVecVarPattern = Pattern.compile("\\\\mathbf\\{([a-zA-Z][a-zA-Z0-9]*)\\}");
		eqnIndexedVecVarPattern = Pattern.compile("\\\\mathbf\\{([a-zA-Z][a-zA-Z0-9]*)\\}_([a-zA-Z][a-zA-Z0-9]*)");
	}

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
		String svg = Tex2SVGKt.tex2svg(eqn, false, BLOCK_EQN_FONT_SIZE);
		String src = "n/a";
		try {
			src = outputDir+"/images/t"+eqnCounter+".svg";
			Path outpath = Paths.get(src);
			System.out.println(outpath);
			Files.write(outpath, svg.getBytes());
			eqnCounter++;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return new BlockImage(src,"");
//		return new BlockEquation(svg);
	}

	@Override
	public OutputModelObject visitEqn_content(BookishParser.Eqn_contentContext ctx) {
		String eqn = ctx.getText();

		// check for special cases like $w$ and $\mathbf{w}_i$.
		List<String> elements = extract(eqnVarPattern, eqn);
		if ( elements.size()>0 ) {
			return new EqnVar(elements.get(0));
		}
		elements = extract(eqnVecVarPattern, eqn);
		if ( elements.size()>0 ) {
			return new EqnVecVar(elements.get(0));
		}
		elements = extract(eqnIndexedVarPattern, eqn);
		if ( elements.size()>0 ) {
			return new EqnIndexedVar(elements.get(0),elements.get(1));
		}
		elements = extract(eqnIndexedVecVarPattern, eqn);
		if ( elements.size()>0 ) {
			return new EqnIndexedVecVar(elements.get(0), elements.get(1));
		}

		String svg = Tex2SVGKt.tex2svg(eqn, false, INLINE_EQN_FONT_SIZE);
		String src = "n/a";
		try {
			src = outputDir+"/images/t"+eqnCounter+".svg";
			Path outpath = Paths.get(src);
			System.out.println(outpath);
			Files.write(outpath, svg.getBytes());
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

	@Override
	public OutputModelObject visitBlock_image(BookishParser.Block_imageContext ctx) {
		Map<String,String> attrs = new HashMap<>();
		for (BookishParser.Attr_assignmentContext a : ctx.attr_assignment()) {
			String name = a.name.getText();
			String value = stripQuotes(a.value.getText());
			attrs.put(name, value);
		}
		return new BlockImage(attrs);
	}

	@Override
	public OutputModelObject visitTable(BookishParser.TableContext ctx) {
		List<TableRow> rows = new ArrayList<>();
		TableRow headers = null;
		if ( ctx.table_header()!=null ) {
			headers = (TableRow) visitTable_header(ctx.table_header());
		}
		for (BookishParser.Table_rowContext row : ctx.table_row()) {
			rows.add( (TableRow)visit(row));
		}
		return new Table(headers, rows);
	}

	@Override
	public OutputModelObject visitTable_header(BookishParser.Table_headerContext ctx) {
		List<TableItem> items = new ArrayList<>();
		for (BookishParser.Table_itemContext el : ctx.table_item()) {
			items.add( (TableItem)visit(el) );
		}
		return new TableRow(items);
	}

	@Override
	public OutputModelObject visitTable_row(BookishParser.Table_rowContext ctx) {
		List<TableItem> items = new ArrayList<>();
		for (BookishParser.Table_itemContext el : ctx.table_item()) {
			items.add( (TableItem)visit(el) );
		}
		return new TableRow(items);
	}

	@Override
	public OutputModelObject visitTable_item(BookishParser.Table_itemContext ctx) {
		List<OutputModelObject> contents = new ArrayList<>();
		for (BookishParser.Section_elementContext el : ctx.section_element()) {
			contents.add( visit(el) );
		}
		return new TableItem(contents);
	}

	@Override
	public OutputModelObject visitOrdered_list(BookishParser.Ordered_listContext ctx) {
		// 		( ws? LI list_item )+
		List<ListItem> items = new ArrayList<>();
		for (BookishParser.List_itemContext el : ctx.list_item()) {
			items.add((ListItem)visit(el));
		}
		return new OrderedList(items);
	}

	@Override
	public OutputModelObject visitUnordered_list(BookishParser.Unordered_listContext ctx) {
		List<ListItem> items = new ArrayList<>();
		for (BookishParser.List_itemContext el : ctx.list_item()) {
			items.add((ListItem)visit(el));
		}
		return new UnOrderedList(items);
	}

	@Override
	public OutputModelObject visitList_item(BookishParser.List_itemContext ctx) {
		List<OutputModelObject> elements = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			elements.add( visit(el) );
		}
		return new ListItem(elements);
	}

	@Override
	public OutputModelObject visitXml(BookishParser.XmlContext ctx) {
		if ( ctx.start.getType()==END_TAG ) {
			String text = ctx.getText();
			return new XMLEndTag(text.substring(2, text.length()-1));
		}
		String name = ctx.tagname.getText();
		Map<String,String> attrs = new HashMap<>();
		for (BookishParser.Attr_assignmentContext a : ctx.attr_assignment()) {
			String value = stripQuotes(a.value.getText());
			attrs.put(a.name.getText(), value);
		}
		return new XMLTag(name, attrs);
	}

	@Override
	public OutputModelObject visitBold(BookishParser.BoldContext ctx) {
		return new Bold(stripQuotes(ctx.getText(),2));
	}

	@Override
	public OutputModelObject visitItalics(BookishParser.ItalicsContext ctx) {
		return new Italics(stripQuotes(ctx.getText()));
	}

	// Support

	private static List<String> extract(Pattern pattern, String text) {
		Matcher m = pattern.matcher(text);
		List<String> elements = new ArrayList<>();
		if ( m.matches() ) {
			for (int i = 1; i <= m.groupCount(); i++) {
				elements.add(m.group(i));
			}
		}
		return elements;
	}

	/** Remove first and last char from argument */
	public static String stripQuotes(String quotedString) {
		return stripQuotes(quotedString, 1);
	}

	public static String stripQuotes(String quotedString, int n) {
		return quotedString.substring(n, quotedString.length()-n);
	}
}
