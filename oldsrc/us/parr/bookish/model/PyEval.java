package us.parr.bookish.model;

import us.parr.bookish.entity.ExecutableCodeDef;

import java.util.Map;

public class PyEval extends PyDo {
	public String dataType;
	public String displayData;

	public PyEval(ExecutableCodeDef codeDef,
	              String stdout,
	              String stderr,
	              Map<String, String> args,
	              String dataType,
	              String displayData)
	{
		super(codeDef, stdout, stderr, args);
		this.dataType = dataType;
		this.displayData = displayData;
	}
}
