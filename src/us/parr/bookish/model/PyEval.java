package us.parr.bookish.model;

import us.parr.bookish.model.entity.ExecutableCodeDef;

public class PyEval extends PyDo {
	public String dataType;
	public String displayData;

	public PyEval(ExecutableCodeDef codeDef,
	              String stdout,
	              String stderr,
	              String dataType,
	              String displayData)
	{
		super(codeDef, stdout, stderr);
		this.dataType = dataType;
		this.displayData = displayData;
	}
}
