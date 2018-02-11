package us.parr.bookish.model;

import us.parr.bookish.model.entity.ExecutableCodeDef;

public class PyEval extends PyDo {
	public PyEval(ExecutableCodeDef codeDef, String stdout, String stderr, String displayData) {
		super(codeDef, stdout, stderr, displayData);
	}
}
