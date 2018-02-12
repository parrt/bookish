package us.parr.bookish.model;

import us.parr.bookish.model.entity.ExecutableCodeDef;

import java.util.Map;

public class PyFig extends PyDo {
	public PyFig(ExecutableCodeDef codeDef, String stdout, String stderr, Map<String, String> args) {
		super(codeDef, stdout, stderr, args);
	}
}
