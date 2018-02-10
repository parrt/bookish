package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

public class ExecutableCodeDef extends EntityDef {
	public String codeBlock;
	public boolean isCell = true; // default: display, run, show output

	public ExecutableCodeDef(int index, Token refOrStartToken, String codeBlock) {
		super(index, refOrStartToken);
		this.codeBlock = codeBlock;
	}
}
