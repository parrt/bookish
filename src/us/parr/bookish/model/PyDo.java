package us.parr.bookish.model;

import us.parr.bookish.model.entity.ExecutableCodeDef;

/** Execute some python code.
 *  Used as base class; not used in templates as output object.
 */
public abstract class PyDo extends OutputModelObject {
	public ExecutableCodeDef codeDef;
	public String stdout;
	public String stderr;
	public String displayData;

	public PyDo(ExecutableCodeDef codeDef, String stdout, String stderr) {
		this(codeDef, stdout, stderr, null);
	}

	public PyDo(ExecutableCodeDef codeDef, String stdout, String stderr, String displayData) {
		this.codeDef = codeDef;
		if ( stdout!=null ) {
			this.stdout = stdout.trim().length()>0 ? stdout : null;
		}
		if ( stderr!=null ) {
			this.stderr = stderr.trim().length()>0 ? stderr : null;
		}
		this.displayData = displayData;
	}
}
