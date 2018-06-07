package us.parr.bookish.entity;

import org.antlr.v4.runtime.Token;

import static us.parr.lib.ParrtStrings.stripQuotes;

public class UnknownDef extends EntityDef {
	public Token startToken;
	public UnknownDef(Token startToken) {
		super(0,null, startToken);
		this.label = stripQuotes(startToken.getText());
	}

	@Override
	public Token getStartToken() {
		return startToken;
	}
}
