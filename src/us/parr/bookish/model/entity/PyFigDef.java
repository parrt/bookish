package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;
import us.parr.bookish.parse.BookishParser;

public class PyFigDef extends ExecutableCodeDef {
	public BookishParser.PyfigContext tree;

	public String generatedFilename;

	public PyFigDef(BookishParser.PyfigContext tree,
	                String inputFilename,
	                int index,
	                Token startOrRefToken,
	                String code)
	{
		super(inputFilename, index, startOrRefToken, code);
		this.tree = tree;
		this.isOutputVisible = false;
	}
}
