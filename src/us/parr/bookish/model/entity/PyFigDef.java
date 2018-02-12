package us.parr.bookish.model.entity;

import us.parr.bookish.parse.BookishParser;

import java.util.Map;

public class PyFigDef extends ExecutableCodeDef {
	public BookishParser.PyfigContext tree;

	public String generatedFilename;

	public PyFigDef(BookishParser.PyfigContext tree,
	                String inputFilename,
	                int index,
	                Map<String,String> argMap,
	                String code)
	{
		super(inputFilename, index, tree.getStart(), code);
		this.tree = tree;
		this.isOutputVisible = false;
		this.label = argMap.get("label");
		if ( argMap.containsKey("hide") ) isCodeVisible = !argMap.get("hide").equals("true");
	}
}
