package us.parr.bookish.semantics;

import us.parr.bookish.Tool;
import us.parr.bookish.entity.InlinePyEvalDef;
import us.parr.bookish.entity.PyEvalDef;
import us.parr.bookish.entity.PyFigDef;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.ChapDocInfo;
import us.parr.lib.collections.MutableInt;

import java.util.HashMap;
import java.util.Map;

import static us.parr.bookish.entity.ExecutableCodeDef.DEFAULT_CODE_LABEL;

/** A parse tree listener that collects Python code snippets
 *  and annotates the tree with code definitions. Also, updates
 *  {@link ChapDocInfo}'s list of executable python defs.
 */
public class DefPythonEntitiesListener extends BookishBaseListener {
	public ChapDocInfo doc;

	/** Track the snippet index for each label. */
	public Map<String,MutableInt> codeCounters = new HashMap<>();

	public String codefileBasename;

	public DefPythonEntitiesListener(ChapDocInfo doc) {
		this.doc = doc;
		this.codefileBasename = doc.getSourceBaseName();
	}

	@Override
	public void enterPyeval(BookishParser.PyevalContext ctx) {
		String py = null;
		Map<String, String> attrs = ctx.attrs().attributes;
		String label = attrs.get("label");
		if ( label==null ) {
			label= DEFAULT_CODE_LABEL;
		}
		codeCounters.putIfAbsent(label, new MutableInt(1));
		if ( ctx.pycodeblock()!=null ) {
			py = ctx.pycodeblock().getText().trim();
			if ( py.length()==0 ) py = null;
		}
		ctx.codeDef = new PyEvalDef(ctx, codefileBasename, codeCounters.get(label).v, ctx.attrs(), py);
		doc.codeBlocks.add(ctx.codeDef);
		ctx.codeDef.enclosingSection = currentSecPtr;
		ctx.codeDef.enclosingChapter = currentChap;
		codeCounters.get(label).v++;
	}

	@Override
	public void enterPyfig(BookishParser.PyfigContext ctx) {
		Map<String, String> attrs = ctx.attrs().attributes;
		String label = attrs.get("label");
		if ( label==null ) {
			label=DEFAULT_CODE_LABEL;
		}
		codeCounters.putIfAbsent(label, new MutableInt(1));

		String py = ctx.pycodeblock().getText().trim();
		if ( py.length()>0 ) {
			ctx.codeDef = new PyFigDef(ctx, codefileBasename, codeCounters.get(label).v, ctx.attrs(), py);
			doc.codeBlocks.add(ctx.codeDef);
			ctx.codeDef.enclosingSection = currentSecPtr;
			ctx.codeDef.enclosingChapter = currentChap;
		}
		codeCounters.get(label).v++;
	}

	@Override
	public void enterInline_py(BookishParser.Inline_pyContext ctx) {
		String label = Tool.getAttr(ctx, "label");
		if ( label==null ) {
			label=DEFAULT_CODE_LABEL;
		}
		// last line is expression to get output or blank line or comment
		String py = ctx.pycodeblock().getText().trim();
		codeCounters.putIfAbsent(label, new MutableInt(1));
		if ( py.length()==0 ) py = null;
		ctx.codeDef = new InlinePyEvalDef(ctx, codefileBasename, codeCounters.get(label).v, ctx.attrs(), py);
		doc.codeBlocks.add(ctx.codeDef);
		ctx.codeDef.enclosingSection = currentSecPtr;
		ctx.codeDef.enclosingChapter = currentChap;
		codeCounters.get(label).v++;
	}
}
