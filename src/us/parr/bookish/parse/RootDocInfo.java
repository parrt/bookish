package us.parr.bookish.parse;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import us.parr.bookish.semantics.Artifact;

import java.util.Map;

public class RootDocInfo extends DocInfo {
	public Map<String,String> attributes; // book or article attributes

	public RootDocInfo(Artifact artifact, Parser parser, ParserRuleContext tree) {
		super(artifact, parser, tree);
		this.parser = parser;
		this.tree = tree;
		if ( tree instanceof BookishParser.RootdocumentContext ) {
			attributes = ((BookishParser.RootdocumentContext)tree).book().attrs().attributes;
		}
		else {
			// articles have no attributes yet
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
