package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;
import us.parr.bookish.parse.BookishParser;

public class PyEvalDef extends ExecutableCodeDef {
	public BookishParser.PyevalContext tree;

	public PyEvalDef(BookishParser.PyevalContext tree, String inputFilename,
	                 int index,
	                 Token refOrStartToken,
	                 String code,
	                 String displayExpr)
	{
		super(inputFilename, index, refOrStartToken, code, displayExpr);
		this.tree = tree;
	}

	public boolean isOutputVisible() { return true; }
	public boolean isCodeVisible() { return true; }
}
