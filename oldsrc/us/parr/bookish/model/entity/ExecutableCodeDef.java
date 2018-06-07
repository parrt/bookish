package us.parr.bookish.entity;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Map;

import static us.parr.lib.ParrtStrings.expandTabs;

public abstract class ExecutableCodeDef extends EntityDef {
	public ParserRuleContext tree;

	public String code;
	public String displayExpr;
	public String inputFilename;
	public SectionDef enclosingSection;
	public ChapterDef enclosingChapter;

	public boolean isCodeVisible = true;
	public boolean isOutputVisible = true;

	public Map<String,String> attributes;

	public ExecutableCodeDef(ParserRuleContext tree,
	                         String inputFilename,
	                         int index,
	                         Token startOrRefToken,
	                         Map<String,String> attributes,
	                         String code)
	{
		super(index, startOrRefToken);
		this.tree = tree;
		this.inputFilename = inputFilename;
		this.code = expandTabs(code, 4);
		this.attributes = attributes;
		if ( attributes!=null ) {
			this.label = attributes.get("label");
			if ( attributes.containsKey("hide") ) {
				isCodeVisible = !attributes.get("hide").equals("true");
			}
			if ( attributes.containsKey("output") ) {
				displayExpr = attributes.get("output");
			}
		}
	}
}
