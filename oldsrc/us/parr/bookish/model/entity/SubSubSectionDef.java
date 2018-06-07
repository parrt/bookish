package us.parr.bookish.entity;

import org.antlr.v4.runtime.Token;

public class SubSubSectionDef extends SectionDef {
	public SubSubSectionDef(int secNumber, Token titleToken, EntityWithScope enclosingScope) {
		super(secNumber, titleToken, enclosingScope);
	}
}
