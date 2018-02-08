package us.parr.bookish.model.ref;

import org.antlr.v4.runtime.Token;
import us.parr.bookish.model.entity.UnknownDef;

public class UnknownRef extends EntityRef {
	public UnknownRef(Token startToken) {
		super(new UnknownDef(startToken));
	}
}
