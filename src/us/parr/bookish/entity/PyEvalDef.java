package us.parr.bookish.entity;

import org.antlr.v4.runtime.ParserRuleContext;
import us.parr.bookish.parse.BookishParser;

public class PyEvalDef extends ExecutableCodeDef {
	public PyEvalDef(ParserRuleContext tree,
	                 String inputFilename,
	                 int index,
	                 BookishParser.AttrsContext attrsCtx,
	                 String code)
	{
		super(tree, inputFilename, index, attrsCtx, code);
	}
}
