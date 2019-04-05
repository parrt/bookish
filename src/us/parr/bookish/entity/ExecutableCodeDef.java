package us.parr.bookish.entity;

import org.antlr.v4.runtime.ParserRuleContext;
import us.parr.bookish.parse.BookishParser;

import static us.parr.lib.ParrtStrings.expandTabs;

public abstract class ExecutableCodeDef extends EntityDef {
	public static final String DEFAULT_CODE_LABEL = "snippets";

	public ParserRuleContext tree;

	public String code;
	public String displayExpr;
	public String inputFilename;
	public SectionDef enclosingSection;
	public ChapterDef enclosingChapter;

	public boolean isCodeVisible = true;
	public boolean isOutputVisible = true;
	public boolean isEnabled = true;

	public ExecutableCodeDef(ParserRuleContext tree,
	                         String inputFilename,
	                         int index,
	                         BookishParser.AttrsContext attrsCtx,
	                         String code)
	{
		super(index, attrsCtx!=null ? attrsCtx.attributes : null);
		this.tree = tree;
		this.inputFilename = inputFilename;
		this.code = expandTabs(code, 4);
		if ( attrsCtx!=null ) {
			if ( attrsCtx.attributes.containsKey("hide") ) {
				isCodeVisible = !attrsCtx.attributes.get("hide").equals("true");
			}
			if ( attrsCtx.attributes.containsKey("output") ) {
				displayExpr = attrsCtx.attributes.get("output");
			}
			if ( attrsCtx.attributes.containsKey("disable") ) {
				isEnabled = !attrsCtx.attributes.get("disable").equals("true");
			}
		}
		if ( label==null ) {
			label = DEFAULT_CODE_LABEL;
		}
	}
}
