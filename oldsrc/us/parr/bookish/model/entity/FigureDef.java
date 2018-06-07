package us.parr.bookish.entity;

import org.antlr.v4.runtime.Token;

import java.util.Map;

public class FigureDef extends EntityDef {
	public String caption;

	public FigureDef(int index, Token startToken, Map<String,String> args) {
		super(index, startToken);
		this.label = args.get("label");
		this.caption = args.get("caption");
	}

	public boolean isGloballyVisible() { return true; }
}
