package us.parr.bookish.model.entity;

import us.parr.bookish.parse.BookishParser;

import java.util.Map;

public class PyEvalDef extends ExecutableCodeDef {
	public PyEvalDef(BookishParser.PyevalContext tree,
	                 String inputFilename,
	                 int index,
	                 Map<String,String> argMap,
	                 String code)
	{
		super(tree, inputFilename, index, tree.getStart(), argMap, code);
	}
}
