package us.parr.bookish.model.entity;

import org.stringtemplate.v4.ST;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.translate.Translator;

public class EntityDef {
	public String label; // optional label

	/** indexed from 1; fig, citation, sidenote number.
	 *  Does not track chp/sec numbers. Those are done via
	 *  ContainerWithTitle.connectContainerTree().
	 */
	public int index;

	public OutputModelObject model;
	public ST template;

	public EntityDef(int index, String label) {
		this.index = index;
		this.label = Translator.stripCurlies(label);
	}

	public boolean isGloballyVisible() { return false; }
}
