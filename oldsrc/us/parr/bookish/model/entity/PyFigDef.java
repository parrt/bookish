package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

import java.util.Map;

public class PyFigDef extends ExecutableCodeDef {
	public String generatedFilenameNoSuffix; // minus suffix; could be .svg, .png, or .pdf

	public PyFigDef(BookishParser.PyfigContext tree,
	                String inputFilename,
	                int index,
	                Map<String,String> attributes,
	                String code)
	{
		super(tree, inputFilename, index, tree.getStart(), attributes, code);
		this.isOutputVisible = false;
	}
}
