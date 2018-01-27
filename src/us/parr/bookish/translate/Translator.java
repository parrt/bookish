package us.parr.bookish.translate;

import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.model.Abstract;
import us.parr.bookish.model.Author;
import us.parr.bookish.model.BlockEquation;
import us.parr.bookish.model.BlockImage;
import us.parr.bookish.model.Bold;
import us.parr.bookish.model.Chapter;
import us.parr.bookish.model.ContainerWithTitle;
import us.parr.bookish.model.EqnIndexedVar;
import us.parr.bookish.model.EqnIndexedVecVar;
import us.parr.bookish.model.EqnVar;
import us.parr.bookish.model.EqnVecVar;
import us.parr.bookish.model.HyperLink;
import us.parr.bookish.model.InlineEquation;
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
import us.parr.bookish.model.TableHeaderItem;
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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static us.parr.bookish.Tool.outputDir;
import static us.parr.bookish.parse.BookishParser.END_TAG;

public class Translator extends BookishParserBaseVisitor<OutputModelObject> {
	public static int INLINE_EQN_FONT_SIZE = 14;
	public static int BLOCK_EQN_FONT_SIZE = 14;
	public STGroupFile templates = new STGroupFile("templates/HTML.stg");

	public Pattern eqnVarPattern;
	public Pattern eqnVecVarPattern;
	public Pattern eqnIndexedVarPattern;
	public Pattern eqnIndexedVecVarPattern;
	public Pattern sectionAnchorPattern;

	public Translator() {
		eqnVarPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)");
		eqnIndexedVarPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)_([a-zA-Z][a-zA-Z0-9]*)");
		eqnVecVarPattern = Pattern.compile("\\\\mathbf\\{([a-zA-Z][a-zA-Z0-9]*)\\}");
		eqnIndexedVecVarPattern = Pattern.compile("\\\\mathbf\\{([a-zA-Z][a-zA-Z0-9]*)\\}_([a-zA-Z][a-zA-Z0-9]*)");
		sectionAnchorPattern = Pattern.compile(".*\\(([a-zA-Z_][a-zA-Z0-9\\-_]*?)\\)");
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
	public OutputModelObject visitAuthor(BookishParser.AuthorContext ctx) {
		Paragraph para = (Paragraph)visit(ctx.paragraph());
		return new Author(para.elements);
	}

	@Override
	public OutputModelObject visitAbstract_(BookishParser.Abstract_Context ctx) {
		Paragraph para = (Paragraph)visit(ctx.paragraph());
		return new Abstract(para.elements);
	}

	@Override
	public OutputModelObject visitChapter(BookishParser.ChapterContext ctx) {
		String title = ctx.chap.getText();
		title = title.substring(1).trim();
		OutputModelObject auth = null;
		if ( ctx.author()!=null ) {
			auth = visit(ctx.author());
		}
		OutputModelObject abs = null;
		if ( ctx.abstract_()!=null ) {
			abs = visit(ctx.abstract_());
		}
		List<OutputModelObject> elements = new ArrayList<>();
		List<ContainerWithTitle> sections = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			if ( el instanceof BookishParser.AuthorContext ||
				 el instanceof BookishParser.Abstract_Context )
			{
				continue;
			}
			OutputModelObject m = visit(el);
			if ( m instanceof Section ) {
				sections.add((Section)m);
			}
			else {
				elements.add(m);
			}
		}
		return new Chapter(title, null, (Author)auth, (Abstract)abs, elements, sections);
	}


	@Override
	public OutputModelObject visitSection(BookishParser.SectionContext ctx) {
		List<ParseTree> children = ctx.children;
		String title = ctx.sec.getText();
		title = title.substring(2).trim();

		List<String> anchors = extract(sectionAnchorPattern, title);
		String anchor = null;
		if ( anchors.size()>0 ) {
			anchor = anchors.get(0);
			int lparent = title.indexOf('(');
			title = title.substring(0, lparent);
		}

		List<OutputModelObject> elements = new ArrayList<>();
		List<ContainerWithTitle> subsections = new ArrayList<>();
		for (ParseTree el : children) {
			OutputModelObject m = visit(el);
			if ( m instanceof SubSection ) {
				subsections.add((SubSection)m);
			}
			else {
				elements.add(m);
			}
		}
		return new Section(title, anchor, elements, subsections);
	}

	@Override
	public OutputModelObject visitSubsection(BookishParser.SubsectionContext ctx) {
		List<ParseTree> children = ctx.children;
		String title = ctx.sec.getText();
		title = title.substring(3).trim();

		List<String> anchors = extract(sectionAnchorPattern, title);
		String anchor = null;
		if ( anchors.size()>0 ) {
			anchor = anchors.get(0);
			int lparent = title.indexOf('(');
			title = title.substring(0, lparent);
		}

		List<OutputModelObject> elements = new ArrayList<>();
		for (ParseTree el : children) {
			OutputModelObject m = visit(el);
			elements.add(m);
		}
		return new SubSection(title, anchor, elements);
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
	public OutputModelObject visitBlock_eqn(BookishParser.Block_eqnContext ctx) {
		String eqn = stripQuotes(ctx.getText(), 3);

		String relativePath = "images/blkeqn-"+hash(eqn)+".svg";
		String src = outputDir+"/"+relativePath;
		Path outpath = Paths.get(src);
		if ( !Files.exists(outpath) ) {
			String svg = Tex2SVGKt.tex2svg(eqn, true, BLOCK_EQN_FONT_SIZE);
			try {
				System.out.println(outpath);
				Files.write(outpath, svg.getBytes());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return new BlockEquation(relativePath, eqn);
	}

	@Override
	public OutputModelObject visitEqn(BookishParser.EqnContext ctx) {
		String eqn = stripQuotes(ctx.getText());

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
			return new EqnIndexedVar(elements.get(0), elements.get(1));
		}
		elements = extract(eqnIndexedVecVarPattern, eqn);
		if ( elements.size()>0 ) {
			return new EqnIndexedVecVar(elements.get(0), elements.get(1));
		}

		String relativePath = "images/eqn-"+hash(eqn)+".svg";
		String src = outputDir+"/"+relativePath;
		Path outpath = Paths.get(src);
		if ( !Files.exists(outpath) ) {
			String svg = Tex2SVGKt.tex2svg(eqn, false, INLINE_EQN_FONT_SIZE);
			try {
				System.out.println(outpath);
				Files.write(outpath, svg.getBytes());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return new InlineEquation(relativePath, eqn);
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
			TableItem item = (TableItem) visit(el);
			items.add(new TableHeaderItem(item.contents));
		}
		return new TableRow(items);
	}

	@Override
	public OutputModelObject visitTable_row(BookishParser.Table_rowContext ctx) {
		List<TableItem> items = new ArrayList<>();
		for (BookishParser.Table_itemContext el : ctx.table_item()) {
			TableItem item = (TableItem) visit(el);
			items.add(item);
		}
		return new TableRow(items);
	}

	@Override
	public OutputModelObject visitTable_item(BookishParser.Table_itemContext ctx) {
		List<OutputModelObject> contents = new ArrayList<>();
		for (ParseTree child : ctx.children) {
			contents.add( visit(child) );
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

	public static String hash(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(text.getBytes());
			return toHexString(digest);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return "bad-hash";
	}

	@NotNull
	private static String toHexString(byte[] digest) {
		StringBuilder buf = new StringBuilder();
		for (byte b : digest) {
			buf.append(String.format("%02X", b));
		}
		return buf.toString();
	}
}
