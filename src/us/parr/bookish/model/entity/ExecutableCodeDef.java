package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

import static us.parr.lib.ParrtStrings.expandTabs;

public abstract class ExecutableCodeDef extends EntityDef {
	public String code;
	public String displayExpr;
	public String inputFilename;

	public boolean isCodeVisible = true;
	public boolean isOutputVisible = true;

	public ExecutableCodeDef(String inputFilename, int index, Token startOrRefToken, String code) {
		super(index, startOrRefToken);
		this.inputFilename = inputFilename;
		this.code = expandTabs(code, 4);
	}

	public ExecutableCodeDef(String inputFilename, int index, Token refOrStartToken, String code, String displayExpr) {
		this(inputFilename, index, refOrStartToken, code);
		this.displayExpr = displayExpr;
	}
}
