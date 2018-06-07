package us.parr.bookish.entity;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Map;

public class PyEvalDef extends ExecutableCodeDef {
	public PyEvalDef(ParserRuleContext tree,
	                 String inputFilename,
	                 int index,
	                 Map<String,String> attributes,
	                 String code)
	{
		super(tree, inputFilename, index, tree.getStart(), attributes, code);
	}
}
