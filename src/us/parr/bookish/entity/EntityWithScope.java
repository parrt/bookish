package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

public abstract class EntityWithScope extends EntityDef {
	public String title;

	public EntityWithScope(int index,
	                       BookishParser.AttrsContext attrsCtx,
	                       EntityWithScope enclosingScope)
	{
		super(index, attrsCtx.attributes);
		this.enclosingScope = enclosingScope;
		this.title = attrsCtx.attributes.get("title");
		if ( label==null ) {
			label = "sec:"+getEntityID();
		}
	}
}
