package us.parr.bookish.model.entity;

import org.antlr.v4.runtime.Token;

public abstract class EntityWithScope extends EntityDef {
	public EntityWithScope enclosingScope;

	public EntityWithScope(int index, Token startToken, EntityWithScope enclosingScope) {
		super(index, startToken);
		this.enclosingScope = enclosingScope;
	}

	/** Return string indicating which section or chapter number */
	public String getContainerNumber() {
		if ( enclosingScope==null ) {
			return String.valueOf(index);
		}
		return enclosingScope.getContainerNumber()+'.'+String.valueOf(index);
	}
}
