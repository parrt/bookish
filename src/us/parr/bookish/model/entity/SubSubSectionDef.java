package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

public class SubSubSectionDef extends SectionDef {
	public SubSubSectionDef(int secNumber, Token titleToken, EntityWithScope enclosingScope) {
		super(secNumber, titleToken, enclosingScope);
	}
}
