package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class InlinePyEvalDef extends ExecutableCodeDef {
	public InlinePyEvalDef(ParserRuleContext tree,
	                       String inputFilename,
	                       int index,
	                       Token startOrRefToken,
	                       String code) {
		super(tree, inputFilename, index, startOrRefToken, null, code);
		this.isCodeVisible = false;
		displayExpr = code;
		this.code = null;
	}
}
