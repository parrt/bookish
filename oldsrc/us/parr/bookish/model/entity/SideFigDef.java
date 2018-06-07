package us.parr.bookish.entity;

import org.antlr.v4.runtime.Token;

import java.util.Map;

public class SideFigDef extends FigureDef {
	public SideFigDef(int index, Token startToken, Map<String,String> args) {
		super(index, startToken, args);
	}

	public boolean isSideItem() { return true; }
}
