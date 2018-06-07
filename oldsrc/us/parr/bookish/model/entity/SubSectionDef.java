package us.parr.bookish.entity;

import org.antlr.v4.runtime.Token;

public class SubSectionDef extends SectionDef {
	public SubSectionDef(int secNumber, Token titleToken, EntityWithScope enclosingScope) {
		super(secNumber, titleToken, enclosingScope);
	}
}
