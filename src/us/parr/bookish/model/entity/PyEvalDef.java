package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Map;

public class PyEvalDef extends ExecutableCodeDef {
	public PyEvalDef(ParserRuleContext tree,
	                 String inputFilename,
	                 int index,
	                 Map<String,String> argMap,
	                 String code)
	{
		super(tree, inputFilename, index, tree.getStart(), argMap, code);
	}
}
