package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

public class UnknownDef extends EntityDef {
	public UnknownDef(Token startToken) {
		super(0, startToken);
	}
}
