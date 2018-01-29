package us.parr.bookish.translate;

import org.antlr.v4.runtime.misc.Triple;
import org.antlr.v4.runtime.tree.ParseTree;
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
import us.parr.bookish.model.Latex;
import us.parr.bookish.model.ListItem;
import us.parr.bookish.model.OrderedList;
import us.parr.bookish.model.Other;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.model.Paragraph;
import us.parr.bookish.model.PreAbstract;
import us.parr.bookish.model.Section;
import us.parr.bookish.model.SubSection;
import us.parr.bookish.model.SubSubSection;
import us.parr.bookish.model.Table;
import us.parr.bookish.model.TableHeaderItem;
import us.parr.bookish.model.TableItem;
import us.parr.bookish.model.TableRow;
import us.parr.bookish.model.UnOrderedList;
import us.parr.bookish.model.XMLEndTag;
import us.parr.bookish.model.XMLTag;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.BookishParserBaseVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static us.parr.bookish.Tool.outputDir;
import static us.parr.bookish.parse.BookishParser.END_TAG;

public class Translator extends BookishParserBaseVisitor<OutputModelObject> {
	public static int INLINE_EQN_FONT_SIZE = 13;
	public static int BLOCK_EQN_FONT_SIZE = 13;
	public STGroupFile templates;

	public Pattern eqnVarPattern;
	public Pattern eqnVecVarPattern;
	public Pattern eqnIndexedVarPattern;
	public Pattern eqnIndexedVecVarPattern;
//	public Pattern eqnfxPattern;
//	public Pattern eqnfvxPattern;
//	public Pattern eqnvfvxPattern;

	public Pattern sectionAnchorPattern;
	public Pattern latexPattern;

	public Translator(String templateFileName) {
		templates = new STGroupFile(templateFileName);
		templates.registerRenderer(String.class, new us.parr.bookish.translate.LatexEscaper());
		eqnVarPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)");
		eqnIndexedVarPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)_([a-zA-Z][a-zA-Z0-9]*)");
		eqnVecVarPattern = Pattern.compile("\\\\mathbf\\{([a-zA-Z][a-zA-Z0-9]*)\\}");
		eqnIndexedVecVarPattern = Pattern.compile("\\\\mathbf\\{([a-zA-Z][a-zA-Z0-9]*)\\}_([a-zA-Z][a-zA-Z0-9]*)");
//		eqnfxPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*\\([a-zA-Z][a-zA-Z0-9]*\\))");
//		eqnfvxPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*\\(\\\\mathbf\\{[a-zA-Z][a-zA-Z0-9]*\\}\\))");
		sectionAnchorPattern = Pattern.compile(".*\\(([a-zA-Z_][a-zA-Z0-9\\-_]*?)\\)");
		latexPattern = Pattern.compile("\\\\latex\\{\\{(.*?)\\}\\}", Pattern.DOTALL);
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
		return new Author(visit(ctx.paragraph()));
	}

	@Override
	public OutputModelObject visitPreabstract(BookishParser.PreabstractContext ctx) {
		List<OutputModelObject> paras = new ArrayList<>();
		for (ParseTree p : ctx.paragraph()) {
			Paragraph para = (Paragraph) visit(p);
			paras.add(para);
		}
		return new PreAbstract(paras);
	}

	@Override
	public OutputModelObject visitAbstract_(BookishParser.Abstract_Context ctx) {
		List<OutputModelObject> paras = new ArrayList<>();
		for (ParseTree p : ctx.paragraph()) {
			Paragraph para = (Paragraph) visit(p);
			paras.add(para);
		}
		return new Abstract(paras);
	}

	@Override
	public OutputModelObject visitChapter(BookishParser.ChapterContext ctx) {
		String title = ctx.chap.getText();
		title = title.substring(1).trim();
		OutputModelObject auth = null;
		if ( ctx.author()!=null ) {
			auth = visit(ctx.author());
		}
		OutputModelObject preabs = null;
		if ( ctx.preabstract()!=null ) {
			preabs = visit(ctx.preabstract());
		}
		OutputModelObject abs = null;
		if ( ctx.abstract_()!=null ) {
			abs = visit(ctx.abstract_());
		}
		List<OutputModelObject> elements = new ArrayList<>();
		List<ContainerWithTitle> sections = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			if ( el instanceof BookishParser.AuthorContext ||
				el instanceof BookishParser.PreabstractContext ||
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
		return new Chapter(title, null,
		                   (Author)auth, (PreAbstract)preabs,
		                   (Abstract)abs, elements, sections);
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
		List<ContainerWithTitle> subsubsections = new ArrayList<>();
		for (ParseTree el : children) {
			OutputModelObject m = visit(el);
			if ( m instanceof SubSection ) {
				subsubsections.add((SubSection)m);
			}
			else {
				elements.add(m);
			}
		}
		return new SubSection(title, anchor, elements, subsubsections);
	}

	@Override
	public OutputModelObject visitSubsubsection(BookishParser.SubsubsectionContext ctx) {
		List<ParseTree> children = ctx.children;
		String title = ctx.sec.getText();
		title = title.substring(4).trim();

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
		return new SubSubSection(title, anchor, elements);
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
	public OutputModelObject visitLatex(BookishParser.LatexContext ctx) {
		String text = ctx.getText().trim();
		List<String> stuff = extract(latexPattern, text); // \latex{{...}}
		text = stuff.get(0);

		String relativePath = "images/latex-"+hash(text)+".svg";
		String src = outputDir+"/"+relativePath;
		Path outpath = Paths.get(src);
		if ( !Files.exists(outpath) ) {
			Triple<String,Float,Float> results = us.parr.bookish.translate.Tex2SVG.tex2svg(text, us.parr.bookish.translate.Tex2SVG.LatexType.LATEX, BLOCK_EQN_FONT_SIZE);
			String svg = results.a;
			try {
				System.out.println(outpath);
				Files.write(outpath, svg.getBytes());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return new Latex(relativePath, text, text);
	}

	@Override
	public OutputModelObject visitBlock_eqn(BookishParser.Block_eqnContext ctx) {
		String eqn = stripQuotes(ctx.getText(), 3);

		String relativePath = "images/blkeqn-"+hash(eqn)+".svg";
		String src = outputDir+"/"+relativePath;
		Path outpath = Paths.get(src);
		if ( !Files.exists(outpath) ) {
			Triple<String,Float,Float> results = us.parr.bookish.translate.Tex2SVG.tex2svg(eqn, us.parr.bookish.translate.Tex2SVG.LatexType.BLOCKEQN, BLOCK_EQN_FONT_SIZE);
			String svg = results.a;

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

		float height=0, depth = 0;

		String prefix = String.format("eqn-%s",hash(eqn));
//		String prefix = String.format("images/eqn-%s",hash(eqn));
		File[] files =
			new File(outputDir+"/images")
				.listFiles((dir, name) -> name.startsWith(prefix));
		String existing = null;
		if ( files!=null && files.length>0 ) {
			existing = files[0].getName();
			int i = existing.indexOf("-depth");
			int j = existing.indexOf(".svg", i);
			String depthS = existing.substring(i+"-depth".length(), j);
			depth = Float.parseFloat(depthS);
			return new InlineEquation("images/"+existing, eqn, -1, depth);
		}
		Triple<String,Float,Float> results =
			Tex2SVG.tex2svg(eqn, Tex2SVG.LatexType.EQN, INLINE_EQN_FONT_SIZE);
		String svg = results.a;
		height = results.b;
		depth = results.c;
		try {
			String src = String.format("%s/images/%s-depth%06.2f.svg",outputDir,prefix,depth);
			Path outpath = Paths.get(src);
			System.out.println(outpath);
			Files.write(outpath, svg.getBytes());
			String relativePath = String.format("images/%s-depth%06.2f.svg",prefix,depth);
			return new InlineEquation(relativePath, eqn, height, depth);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
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
		return new BlockImage(ctx.attrs().attrMap);
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

	/*
	table_header : TR ws? (TH attrs END_OF_TAG table_item)+ ;
	th_tag : TH attr_assignment+ END_OF_TAG ;
	*/
	@Override
	public OutputModelObject visitTable_header(BookishParser.Table_headerContext ctx) {
		List<TableItem> items = new ArrayList<>();
		for (int i = 0; i<ctx.attrs().size(); i++) {
			BookishParser.AttrsContext attrsOfTH = ctx.attrs().get(i);
			BookishParser.Table_itemContext itemCtx = ctx.table_item().get(i);
			TableItem item = (TableItem) visit(itemCtx);
			items.add(new TableHeaderItem(item.contents,attrsOfTH.attrMap));
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
		return new XMLTag(name, ctx.attrs().attrMap);
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

	public static List<String> extract(Pattern pattern, String text) {
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

	private static String toHexString(byte[] digest) {
		StringBuilder buf = new StringBuilder();
		for (byte b : digest) {
			buf.append(String.format("%02X", b));
		}
		return buf.toString();
	}
}
