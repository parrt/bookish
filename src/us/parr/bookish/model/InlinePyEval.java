package us.parr.bookish.model;

import us.parr.bookish.model.entity.ExecutableCodeDef;

public class InlinePyEval extends PyEval {
	public InlinePyEval(ExecutableCodeDef codeDef,
	                    String stdout,
	                    String stderr,
	                    String dataType,
	                    String displayData)
	{
		super(codeDef, stdout, stderr, null, dataType, displayData);
	}
}
