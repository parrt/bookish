package us.parr.bookish.model;

import us.parr.bookish.entity.ExecutableCodeDef;

import java.util.Map;

/** Execute some python code.
 *  Used as base class; not used in templates as output object.
 */
public abstract class PyDo extends OutputModelObject {
	public ExecutableCodeDef codeDef;
	public String stdout;
	public String stderr;
	public Map<String, String> args;

	public PyDo(ExecutableCodeDef codeDef, String stdout, String stderr, Map<String, String> args) {
		this.codeDef = codeDef;
		this.args = args;
		if ( stdout!=null ) {
			this.stdout = stdout.trim().length()>0 ? stdout : null;
		}
		if ( stderr!=null ) {
			this.stderr = stderr.trim().length()>0 ? stderr : null;
		}
	}
}
