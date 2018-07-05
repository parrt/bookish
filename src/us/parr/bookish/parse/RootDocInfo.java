package us.parr.bookish.parse;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import us.parr.bookish.semantics.Artifact;

import java.util.Map;

public class RootDocInfo extends DocInfo {
	public Map<String,String> attributes; // book or article attributes

	public RootDocInfo(Artifact artifact, Parser parser, ParserRuleContext tree) {
		super(artifact, parser, tree);
		this.parser = parser;
		this.tree = tree;
		ParseTree rootTag = tree.getChild(0);
		if ( rootTag instanceof BookishParser.BookContext ) {
			attributes = ((BookishParser.BookContext)rootTag).attrs().attributes;
		}
		else {
			if ( ((BookishParser.ArticleContext)rootTag).attrs()!=null ) {
				attributes = ((BookishParser.ArticleContext) rootTag).attrs().attributes;
			}
		}
	}

	public String getGeneratedFilename(String target) {
		String outFilename = "index.html";
		if ( target.equals("latex") ) {
			outFilename = getSourceBaseName()+".tex";
		}
		return outFilename;
	}

	public BookishParser.RootdocumentContext getTreeAsRoot() { return (BookishParser.RootdocumentContext)tree; }
}
