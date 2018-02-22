package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

import java.util.Map;

public class SideFigDef extends FigureDef {
	public SideFigDef(int index, Token startToken, Map<String,String> args) {
		super(index, startToken, args);
	}
}
