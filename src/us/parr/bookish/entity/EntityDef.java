package us.parr.bookish.entity;

import org.antlr.v4.runtime.Token;
import org.stringtemplate.v4.ST;
import us.parr.bookish.model.OutputModelObject;

import java.util.Map;
import java.util.Objects;

public class EntityDef {
	public String label; // optional label

	/** indexed from 1; fig, citation, sidenote number.
	 *  Does not track chp/sec numbers. Those are done via
	 *  ContainerWithTitle.connectContainerTree().
	 */
	public int index;

	/** Useful to determine if we should generate code for this ref.
	 *  We avoid dup code gen this way.
	 */
	public int refCount = 0;

	public Map<String,String> attributes;
	public OutputModelObject model;
	public ST template;
	public Token startToken;

	public EntityDef(int index, Map<String,String> attributes) {
		this(index, attributes, null);
	}

	public EntityDef(int index, Map<String,String> attributes, Token startToken) {
		this.index = index;
		this.attributes = attributes;
		this.startToken = startToken;
		if ( attributes!=null ) { // get a common attribute
			this.label = attributes.get("label");
		}
	}

	public boolean isGloballyVisible() { return false; }

	public Token getStartToken() { return startToken; }

	public boolean isSideItem() { return false; }

	@Override
	public boolean equals(Object o) {
		if ( this==o ) return true;
		if ( !(o instanceof EntityDef) ) return false;
		EntityDef entityDef = (EntityDef) o;
		return Objects.equals(label, entityDef.label);
	}

	@Override
	public int hashCode() {
		return Objects.hash(label);
	}

	@Override
	public String toString() {
		return '<'+label+":"+this.getClass().getSimpleName()+'>';
	}
}
