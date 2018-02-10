package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

public class CodeDef extends EntityDef {
	public String codeBlock;
	public boolean isCell = true; // default: display, run, show output

	public CodeDef(int index, Token refOrStartToken, String codeBlock) {
		super(index, refOrStartToken);
		this.codeBlock = codeBlock;
	}
}
