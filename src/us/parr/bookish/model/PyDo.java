package us.parr.bookish.model;

import us.parr.bookish.model.entity.ExecutableCodeDef;

/** Execute some python code; no output, no display of code.
 *  Used as base class; not used in templates as output object.
 */
public abstract class PyDo extends OutputModelObject {
	public ExecutableCodeDef codeDef;

	public PyDo(ExecutableCodeDef codeDef) {
		this.codeDef = codeDef;
	}
}
