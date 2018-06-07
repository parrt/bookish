package us.parr.bookish.parse;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class RootDocInfo extends DocInfo {
	public RootDocInfo(Parser parser, ParserRuleContext tree) {
		super(parser, tree);
		this.parser = parser;
		this.tree = tree;
	}

	public BookishParser.RootdocumentContext getTreeAsRoot() { return (BookishParser.RootdocumentContext)tree; }
}
