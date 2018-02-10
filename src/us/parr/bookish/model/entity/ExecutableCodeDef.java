package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

public class ExecutableCodeDef extends EntityDef {
	public String code;
	public boolean isOutputVisible = true;  // default: run, show output
	public boolean isCodeVisible = true;    // default: display code in document

	public String inputFilename;

	public ExecutableCodeDef(String inputFilename, int index, Token refOrStartToken, String code) {
		super(index, refOrStartToken);
		this.inputFilename = inputFilename;
		this.code = code;
	}
}
