package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

public abstract class ExecutableCodeDef extends EntityDef {
	public String code;
	public String displayExpr;
	public String inputFilename;

	public boolean isCodeVisible = true;
	public boolean isOutputVisible = true;

	public ExecutableCodeDef(String inputFilename, int index, Token startOrRefToken, String code) {
		super(index, startOrRefToken);
		this.inputFilename = inputFilename;
		this.code = code;
	}

	public ExecutableCodeDef(String inputFilename, int index, Token refOrStartToken, String code, String displayExpr) {
		this(inputFilename, index, refOrStartToken, code);
		this.displayExpr = displayExpr;
	}
}
