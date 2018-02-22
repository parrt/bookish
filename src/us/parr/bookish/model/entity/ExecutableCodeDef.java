package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Map;

import static us.parr.lib.ParrtStrings.expandTabs;

public abstract class ExecutableCodeDef extends EntityDef {
	public ParserRuleContext tree;

	public String code;
	public String displayExpr;
	public String inputFilename;

	public boolean isCodeVisible = true;
	public boolean isOutputVisible = true;

	public Map<String,String> argMap;

	public ExecutableCodeDef(ParserRuleContext tree,
	                         String inputFilename,
	                         int index,
	                         Token startOrRefToken,
	                         Map<String,String> argMap,
	                         String code)
	{
		super(index, startOrRefToken);
		this.tree = tree;
		this.inputFilename = inputFilename;
		this.code = expandTabs(code, 4);
		this.argMap = argMap;
		if ( argMap!=null ) {
			this.label = argMap.get("label");
			if ( argMap.containsKey("hide") ) {
				isCodeVisible = !argMap.get("hide").equals("true");
			}
			if ( argMap.containsKey("output") ) {
				displayExpr = argMap.get("output");
			}
		}
	}
}
