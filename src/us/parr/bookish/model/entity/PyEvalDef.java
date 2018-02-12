package us.parr.bookish.model.entity;

import us.parr.bookish.parse.BookishParser;

import java.util.Map;

public class PyEvalDef extends ExecutableCodeDef {
	public BookishParser.PyevalContext tree;

	public PyEvalDef(BookishParser.PyevalContext tree, String inputFilename,
	                 int index,
	                 Map<String,String> argMap,
	                 String code,
	                 String displayExpr)
	{
		super(inputFilename, index, tree.getStart(), code, displayExpr);
		this.tree = tree;
		this.label = argMap.get("label");
		if ( argMap.containsKey("hide") ) isCodeVisible = !argMap.get("hide").equals("true");
	}
}
