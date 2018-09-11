package us.parr.bookish.translate;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;
import us.parr.bookish.Tool;
import us.parr.bookish.entity.CalloutDef;
import us.parr.bookish.entity.ChapterDef;
import us.parr.bookish.entity.CitationDef;
import us.parr.bookish.entity.EntityDef;
import us.parr.bookish.entity.FigureDef;
import us.parr.bookish.entity.SectionDef;
import us.parr.bookish.entity.SideFigDef;
import us.parr.bookish.entity.SideNoteDef;
import us.parr.bookish.entity.SideQuoteDef;
import us.parr.bookish.entity.SiteDef;
import us.parr.bookish.entity.SubSectionDef;
import us.parr.bookish.entity.SubSubSectionDef;
import us.parr.bookish.model.Abstract;
import us.parr.bookish.model.Aside;
import us.parr.bookish.model.BlockCode;
import us.parr.bookish.model.BlockEquation;
import us.parr.bookish.model.BlockImage;
import us.parr.bookish.model.Bold;
import us.parr.bookish.model.Callout;
import us.parr.bookish.model.ChapQuote;
import us.parr.bookish.model.Chapter;
import us.parr.bookish.model.Citation;
import us.parr.bookish.model.ContainerWithTitle;
import us.parr.bookish.model.EqnIndexedVar;
import us.parr.bookish.model.EqnIndexedVecVar;
import us.parr.bookish.model.EqnVar;
import us.parr.bookish.model.EqnVecVar;
import us.parr.bookish.model.HyperLink;
import us.parr.bookish.model.Image;
import us.parr.bookish.model.InlineCode;
import us.parr.bookish.model.InlineEquation;
import us.parr.bookish.model.InlinePyEval;
import us.parr.bookish.model.Italics;
import us.parr.bookish.model.Join;
import us.parr.bookish.model.Latex;
import us.parr.bookish.model.LineBreak;
import us.parr.bookish.model.ListItem;
import us.parr.bookish.model.OrderedList;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.model.Paragraph;
import us.parr.bookish.model.PreAbstract;
import us.parr.bookish.model.PyEval;
import us.parr.bookish.model.PyEvalDataFrame;
import us.parr.bookish.model.PyFig;
import us.parr.bookish.model.Quoted;
import us.parr.bookish.model.RawHTML;
import us.parr.bookish.model.Section;
import us.parr.bookish.model.SideFigure;
import us.parr.bookish.model.SideNote;
import us.parr.bookish.model.SideQuote;
import us.parr.bookish.model.Site;
import us.parr.bookish.model.SubSection;
import us.parr.bookish.model.SubSubSection;
import us.parr.bookish.model.TODO;
import us.parr.bookish.model.Table;
import us.parr.bookish.model.TableHeaderItem;
import us.parr.bookish.model.TableItem;
import us.parr.bookish.model.TableRow;
import us.parr.bookish.model.Text;
import us.parr.bookish.model.UnOrderedList;
import us.parr.bookish.model.UnknownSymbol;
import us.parr.bookish.model.ref.CalloutRef;
import us.parr.bookish.model.ref.ChapterRef;
import us.parr.bookish.model.ref.CitationRef;
import us.parr.bookish.model.ref.EntityRef;
import us.parr.bookish.model.ref.FigureRef;
import us.parr.bookish.model.ref.SectionRef;
import us.parr.bookish.model.ref.SideNoteRef;
import us.parr.bookish.model.ref.SiteRef;
import us.parr.bookish.model.ref.UnknownRef;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.BookishParserBaseVisitor;
import us.parr.bookish.parse.DocInfo;
import us.parr.bookish.semantics.Artifact;
import us.parr.bookish.util.DataTable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static us.parr.bookish.Tool.getAttr;
import static us.parr.bookish.semantics.ConvertLatexToImageListener.eqnIndexedVarPattern;
import static us.parr.bookish.semantics.ConvertLatexToImageListener.eqnIndexedVecVarPattern;
import static us.parr.bookish.semantics.ConvertLatexToImageListener.eqnIndexedVecVarPattern2;
import static us.parr.bookish.semantics.ConvertLatexToImageListener.eqnVarPattern;
import static us.parr.bookish.semantics.ConvertLatexToImageListener.eqnVecVarPattern;
import static us.parr.bookish.semantics.ConvertLatexToImageListener.eqnVecVarPattern2;
import static us.parr.bookish.semantics.ConvertLatexToImageListener.extract;
import static us.parr.lib.ParrtCollections.join;
import static us.parr.lib.ParrtStrings.stripQuotes;

/** A translator for a input chapter documents */
public class Translator extends BookishParserBaseVisitor<OutputModelObject> {
	public static int INLINE_EQN_FONT_SIZE = 13;
	public static int BLOCK_EQN_FONT_SIZE = 13;
	public static Map<Class<? extends EntityDef>,Class<? extends EntityRef>> defToRefMap =
		new HashMap<>() {{
			put(CitationDef.class, CitationRef.class);
			put(FigureDef.class, FigureRef.class);
			put(SideFigDef.class, FigureRef.class);
			put(SideNoteDef.class, SideNoteRef.class);
			put(SideQuoteDef.class, SideNoteRef.class);
			put(SiteDef.class, SiteRef.class);
			put(SectionDef.class, SectionRef.class);
			put(SubSectionDef.class, SectionRef.class);
			put(SubSubSectionDef.class, SectionRef.class);
			put(ChapterDef.class, ChapterRef.class);
			put(CalloutDef.class, CalloutRef.class);
		}};

	public Tool tool;
	public Artifact artifact;

	/** Set by visit(tree, doc) so we know which doc we are translating. */
	public DocInfo docInfo;

	public Translator(DocInfo docInfo) {
		this.artifact = docInfo.artifact;
		this.tool = artifact.tool;
		this.docInfo = docInfo;
	}

	/** Find all x={...} attributes and translate those from bookish to appropriate
	 *  target output format.  Replace existing attribute value with translation.
	 *
	 *  Side effect: alters xml attribute map annotation of attrs rule nodes.
	 */
	public void translateXMLAttributes(DocInfo docInfo) {
		Collection<ParseTree> attrsNodes = XPath.findAll(docInfo.tree, "//attrs", docInfo.parser);
		for (ParseTree attrsNode : attrsNodes) {
			for (int i = 0; i<attrsNode.getChildCount(); i++) {
				ParseTree assignment = attrsNode.getChild(i);
				ParseTree key = assignment.getChild(0);
				ParseTree value = assignment.getChild(2);
				if ( key!=null && value!=null ) {
					String v = value.getText();
					if ( v.charAt(0)=='{' ) {
						v = stripQuotes(v);
						BookishParser.AttrsContext a = (BookishParser.AttrsContext) attrsNode;
						String location = docInfo.getSourceName()+" "+a.start.getLine()+":"+a.start.getCharPositionInLine();
						v = tool.translateString(docInfo, v, location);
						a.attributes.put(key.getText(), v);
//						System.out.println("ALTER "+key.getText()+" from "+value.getText()+" ->" + v);
					}
				}
			}
		}
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
			for (OutputModelObject element : ((Join) aggregate).elements) {
				if ( element!=null ) {
					elements.add(element);
				}
			}
			elements.add(nextResult);
			return new Join(elements);
		}
		return new Join(aggregate, nextResult);
	}

	@Override
	public OutputModelObject visitChapter(BookishParser.ChapterContext ctx) {
		OutputModelObject abstract_ = null;
		if ( ctx.abstract_()!=null ) {
			abstract_ = visit(ctx.abstract_());
		}

		List<ContainerWithTitle> sections = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			if ( el instanceof BookishParser.SectionContext ) {
				OutputModelObject m = visit(el);
				sections.add((Section)m);
			}
		}
		Join contentModel = getContentModel(ctx.content());
		Chapter chapter = new Chapter(artifact,
		                              docInfo,
		                              ctx.def,
		                              (PreAbstract) null,
		                              (Abstract) abstract_,
		                              contentModel,
		                              sections);
		return chapter;
	}

	@Override
	public OutputModelObject visitAbstract_(BookishParser.Abstract_Context ctx) {
		Join contentModel = (Join)visit(ctx.content());
		return new Abstract(contentModel.elements);
	}

	@Override
	public OutputModelObject visitSection(BookishParser.SectionContext ctx) {
		String title = getAttr(ctx, "title");

		List<ContainerWithTitle> subsections = new ArrayList<>();
		for (ParseTree el : ctx.subsection()) {
			subsections.add((SubSection)visit(el));
		}
		OutputModelObject content = getContentModel(ctx.content());
		return new Section(ctx.def, title, content, subsections);
	}

	@Override
	public OutputModelObject visitSubsection(BookishParser.SubsectionContext ctx) {
		String title = getAttr(ctx, "title");

		List<ContainerWithTitle> subsubsections = new ArrayList<>();
		for (ParseTree el : ctx.subsubsection()) {
			subsubsections.add((SubSection)visit(el));
		}
		OutputModelObject content = getContentModel(ctx.content());
		return new SubSection(ctx.def, title, content, subsubsections);
	}

	@Override
	public OutputModelObject visitSubsubsection(BookishParser.SubsubsectionContext ctx) {
		String title = getAttr(ctx, "title");

		OutputModelObject content = getContentModel(ctx.content());
		return new SubSubSection(ctx.def, title, content);
	}

	@Override
	public OutputModelObject visitContent(BookishParser.ContentContext ctx) {
		List<OutputModelObject> elements = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			OutputModelObject m = visit(el);
			if ( m!=null ) {
				elements.add(m);
			}
		}
		return new Join(elements);
	}

	@Override
	public OutputModelObject visitLatex(BookishParser.LatexContext ctx) {
		// relativeImageFilename is null implies must be html not latex output
		String latex = ctx.latex_content().getText();
		return new Latex(this, ctx.relativeImageFilename, latex, latex);
	}

	@Override
	public OutputModelObject visitHtml(BookishParser.HtmlContext ctx) {
		String html = ctx.html_content().getText();
		return new RawHTML(html);
	}

	@Override
	public OutputModelObject visitBlock_eqn(BookishParser.Block_eqnContext ctx) {
		String latex = ctx.BLOCK_EQN().getText();
		return new BlockEquation(this, ctx.relativeImageFilename, latex);
	}

	@Override
	public OutputModelObject visitEqn(BookishParser.EqnContext ctx) {
		String eqn = stripQuotes(ctx.getText());

		if ( tool.isLatexTarget() ) {
			return new InlineEquation(null, eqn, -1, -1);
		}

		// check for special cases like $w$ and $\mathbf{w}_i$. Use html
		// not latex for these to reduce number of images.
		List<String> elements = extract(eqnVarPattern, eqn);
		if ( elements.size()>0 ) {
			return new EqnVar(elements.get(0));
		}
		elements = extract(eqnVecVarPattern, eqn);
		if ( elements.size()>0 ) {
			return new EqnVecVar(elements.get(0));
		}
		elements = extract(eqnVecVarPattern2, eqn);
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
		elements = extract(eqnIndexedVecVarPattern2, eqn);
		if ( elements.size()>0 ) {
			return new EqnIndexedVecVar(elements.get(0), elements.get(1));
		}

		return new InlineEquation(ctx.relativeImageFilename, eqn, ctx.height, ctx.depth);
	}

	@Override
	public OutputModelObject visitLink(BookishParser.LinkContext ctx) {
		String txt = ctx.getText();
		int middle = txt.indexOf("]("); // e.g., [name](link)
		String title = txt.substring(1,middle);
		String href = txt.substring(middle+2,txt.length()-1);
		if ( href.contains("\\)") ) {
			href = href.replace("\\)", ")");
		}
		return new HyperLink(title, href);
	}

	@Override
	public OutputModelObject visitBlock_image(BookishParser.Block_imageContext ctx) {
		return new BlockImage(this, ctx.image().attrs().attributes);
	}

	@Override
	public OutputModelObject visitImage(BookishParser.ImageContext ctx) {
		return new Image(this, ctx.attrs().attributes);
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
table_header : TR table_header_item+ ;
table_header_item : ws? TH attrs[List.of("width")]? END_TAG table_item ;
table_row : TR (ws? TD table_item)+ ;
	*/
	@Override
	public OutputModelObject visitTable_header(BookishParser.Table_headerContext ctx) {
		List<TableItem> items = new ArrayList<>();
		for (BookishParser.Table_header_itemContext itemCtx : ctx.table_header_item()) {
			TableItem item = (TableItem) visit(itemCtx);
			BookishParser.AttrsContext attrsOfTH = itemCtx.attrs();
			Map<String, String> attributes = attrsOfTH!=null ? attrsOfTH.attributes : Collections.emptyMap();
			items.add(new TableHeaderItem(item.contents, attributes));
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
		if ( ctx.children!=null ) {
			for (ParseTree child : ctx.children) {
				contents.add(visit(child));
			}
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
	public OutputModelObject visitQuoted(BookishParser.QuotedContext ctx) {
		List<OutputModelObject> elements = new ArrayList<>();
		for (ParseTree el : ctx.children) {
			elements.add( visit(el) );
		}
		return new Quoted(elements);
	}

	@Override
	public OutputModelObject visitInline_code(BookishParser.Inline_codeContext ctx) {
		return new InlineCode(stripQuotes(ctx.INLINE_CODE().getText()));
	}

	@Override
	public OutputModelObject visitFirstuse(BookishParser.FirstuseContext ctx) {
		// \first{model}
		String text = ctx.FIRSTUSE().getText();
		text = text.substring("\\first".length());
		text = stripQuotes(text); // remove {...}
		return new Italics(text); // can't have markup inside
	}

	@Override
	public OutputModelObject visitTodo(BookishParser.TodoContext ctx) {
		// \todo{model}
		String text = ctx.TODO().getText();
		text = text.substring("\\todo".length());
		text = stripQuotes(text); // remove {...}
		return new TODO(text); // can't have markup inside
	}

	@Override
	public OutputModelObject visitBold(BookishParser.BoldContext ctx) {
		return new Bold(stripQuotes(ctx.getText(), 2));
	}

	@Override
	public OutputModelObject visitItalics(BookishParser.ItalicsContext ctx) {
		return new Italics(stripQuotes(ctx.getText()));
	}

	@Override
	public OutputModelObject visitDollar(BookishParser.DollarContext ctx) {
		return new Text("$");
	}

	@Override
	public OutputModelObject visitLt(BookishParser.LtContext ctx) {
		return new Text("<");
	}

	@Override
	public OutputModelObject visitSymbol(BookishParser.SymbolContext ctx) {
		String text = ctx.SYMBOL().getText();
		text = text.substring("\\symbol".length());
		text = stripQuotes(text); // remove {...}
		Map<String, Object> symbols = artifact.templates.rawGetDictionary("symbols");
		if ( symbols==null ) {
			System.err.println("No symbols dictionary in the template file");
			return new Text("[?]");
		}
		Object s = symbols.get(text);
		if ( s==null ) {
			String location = docInfo.getSourceName()+" "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine();
			System.err.println(location+" symbol not defined '"+text+"'");
			return new UnknownSymbol(text);
		}
		return new Text((String) s);
	}

	@Override
	public OutputModelObject visitChapquote(BookishParser.ChapquoteContext ctx) {
		String quote = getAttr(ctx, "quote");
		String author = getAttr(ctx, "author");

		return new ChapQuote(quote, author);
	}

	@Override
	public OutputModelObject visitLinebreak(BookishParser.LinebreakContext ctx) {
		return new LineBreak();
	}

	@Override
	public OutputModelObject visitSite(BookishParser.SiteContext ctx) {
		String label = getAttr(ctx, "label");
		EntityDef def = docInfo.getEntity(label);
		if ( def==null ) {
			return null;
		}
		def.model = new Site((SiteDef)def);

		// Don't add this to the output file, just set model into def object
		// This will appear in output in response to first ref
		return null;
	}

	@Override
	public OutputModelObject visitCitation(BookishParser.CitationContext ctx) {
		String label = getAttr(ctx, "label");
		EntityDef def = docInfo.getEntity(label);
		if ( def==null ) {
			return null;
		}

		def.model = new Citation((CitationDef) def);
		// Don't add this to the output file, just set model into def object
		// This will appear in output in response to first ref
		return null;
	}

	@Override
	public OutputModelObject visitSidequote(BookishParser.SidequoteContext ctx) {
		String label = getAttr(ctx, "label");
		EntityDef def = docInfo.getEntity(label);
		if ( def==null ) {
			return null;
		}

		SideQuote q = new SideQuote((SideQuoteDef)def);
		def.model = q;

		if ( label==null ) {
			return q; // if no label, insert inline here
		}
		return null;
	}

	@Override
	public OutputModelObject visitSidenote(BookishParser.SidenoteContext ctx) {
		String label = getAttr(ctx, "label");
		EntityDef def = docInfo.getEntity(label);
		if ( def==null ) {
			return null;
		}
		SideNote n = new SideNote(def, visit(ctx.content()));
		def.model = n;

		if ( label==null ) {
			return n; // if no label, insert inline here at this point in document model
		}
		return null;
	}

	// figure    : FIGURE attrs END_OF_TAG paragraph_content END_FIGURE
	@Override
	public OutputModelObject visitSidefig(BookishParser.SidefigContext ctx) {
		String label = getAttr(ctx, "label");
		EntityDef def = docInfo.getEntity(label);
		if ( def==null ) {
			return null;
		}

		OutputModelObject fig = visit(ctx.content());
		SideFigure f = new SideFigure(def, label, fig);
		def.model = f;

		if ( label==null ) {
			return f; // if no label, insert inline here at this point in document model
		}
		return null;
	}

	@Override
	public OutputModelObject visitCallout(BookishParser.CalloutContext ctx) {
		String label = getAttr(ctx, "label");
		EntityDef def = docInfo.getEntity(label);
		if ( def==null ) {
			return null;
		}
		SideNote n = new Callout(def, visit(ctx.content()));
		def.model = n;

		if ( label==null ) {
			return n; // if no label, insert inline here at this point in document model
		}
		return null;
	}

	@Override
	public OutputModelObject visitAside(BookishParser.AsideContext ctx) {
		return new Aside(visit(ctx.content()), ctx.attrs().attributes);
	}

	@Override
	public OutputModelObject visitRef(BookishParser.RefContext ctx) {
		String label = stripQuotes(ctx.REF().getText()); // [label]
		EntityDef def = docInfo.getEntity(label);
		System.out.println("Ref to "+def);
		if ( def==null ) {
			System.err.printf("line %d: Unknown label '%s'\n", ctx.start.getLine(), label);
			return new UnknownRef(ctx.REF().getSymbol());
		}

		Class<? extends EntityRef> refClass = defToRefMap.get(def.getClass());
		try {
			Constructor<? extends EntityRef> ctor = refClass.getConstructor(EntityDef.class);
			EntityRef entityRef = ctor.newInstance(def);
//			return new Text("foo");
			return entityRef;
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	/** ```foo``` Just show it */
	@Override
	public OutputModelObject visitCodeblock(BookishParser.CodeblockContext ctx) {
		return new BlockCode(stripQuotes(ctx.getText(), 3).trim());
	}

	@Override
	public OutputModelObject visitPyfig(BookishParser.PyfigContext ctx) {
		return new PyFig(this, ctx.codeDef, ctx.stdout, ctx.stderr, ctx.attrs().attributes);
	}

	@Override
	public OutputModelObject visitPyeval(BookishParser.PyevalContext ctx) {
		Map<String, String> args = ctx.attrs().attributes;
		if ( ctx.displayData!=null ) {
			String[] dataA = ctx.displayData.split("\n");
			String type = dataA[0];
			String data = null;
			if ( dataA.length>1 ) {
				data = join(Arrays.copyOfRange(dataA, 1, dataA.length), "\n");
			}
			if ( type.equals("DataFrame") ) {
				DataTable dataTable = new DataTable(data);
				return new PyEvalDataFrame(ctx.codeDef, ctx.stdout, ctx.stderr, args, type, dataTable);
			}
			else {
				return new PyEval(ctx.codeDef, ctx.stdout, ctx.stderr, args, type, data);
			}
		}
		else {
			return new PyEval(ctx.codeDef, ctx.stdout, ctx.stderr, args, null, null);
		}
	}

	@Override
	public OutputModelObject visitInline_py(BookishParser.Inline_pyContext ctx) {
		if ( ctx.displayData!=null ) {
			String[] dataA = ctx.displayData.split("\n");
			String type = dataA[0];
			String data = null;
			if ( dataA.length>1 ) {
				data = join(Arrays.copyOfRange(dataA, 1, dataA.length), "\n");
				if ( dataA.length==2 && type.startsWith("float") ) {
					data = String.format("%.4f", Double.parseDouble(dataA[1]));
				}
			}
			return new InlinePyEval(ctx.codeDef, ctx.stdout, ctx.stderr, type, data);
		}
		return null;
	}

	@Override
	public OutputModelObject visitParagraph(BookishParser.ParagraphContext ctx) {
		return new Paragraph(visit(ctx.paragraph_content()), ctx.entitiesRefd);
	}

	@Override
	public OutputModelObject visitText(BookishParser.TextContext ctx) {
		return new Text(ctx.start.getText());
	}

	// Support

	public Join getContentModel(BookishParser.ContentContext contentCtx) {
		if ( contentCtx==null ) return null;
		List<OutputModelObject> content = new ArrayList<>();
		for (ParseTree t : contentCtx.children) {
			content.add(visit(t));
		}
		return new Join(content);
	}

	public String processImageWidth(String width) {
		if ( width!=null ) {
			if ( width.endsWith("%") ) {
				// drop %; latex templates will use \linewidth and html will put % back on
				width = width.replace("%", "");
			}
			else {
				System.err.println("PYFIG width "+width+" must be in % units");
				width = "100";
			}
			if ( tool.isLatexTarget() ) {
				width = String.valueOf(Float.valueOf(width)/100.0); // convert to 0..1
			}
			else {
				width = width + "%";
			}
		}
		return width;
	}
}

