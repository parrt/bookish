package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

public class ExecutableCodeDef extends EntityDef {
	public String code;
	public String displayExpr;

	public String inputFilename;

	public ExecutableCodeDef(String inputFilename, int index, Token refOrStartToken, String code) {
		super(index, refOrStartToken);
		this.inputFilename = inputFilename;
		this.code = code;
	}

	public ExecutableCodeDef(String inputFilename, int index, Token refOrStartToken, String code, String displayExpr) {
		this(inputFilename, index, refOrStartToken, code);
		this.displayExpr = displayExpr;
	}

	public boolean isOutputVisible() { return false; }  // default: run, don't show output
	public boolean isCodeVisible() { return false; }    // default: don't display code in document
}
