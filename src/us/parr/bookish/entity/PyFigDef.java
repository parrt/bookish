package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public class PyFigDef extends ExecutableCodeDef {
	public String generatedFilenameNoSuffix; // minus suffix; could be .svg, .png, or .pdf

	public PyFigDef(BookishParser.PyfigContext tree,
	                String inputFilename,
	                int index,
	                BookishParser.AttrsContext attrsCtx,
	                String code)
	{
		super(tree, inputFilename, index, attrsCtx, code);
		this.isOutputVisible = false;
	}
}
