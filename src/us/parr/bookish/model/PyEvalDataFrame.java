package us.parr.bookish.model;

import us.parr.bookish.entity.ExecutableCodeDef;
import us.parr.bookish.util.DataTable;

import java.util.Map;

public class PyEvalDataFrame extends PyEval {
	public DataTable dataTable;

	public PyEvalDataFrame(ExecutableCodeDef codeDef,
	                       String stdout,
	                       String stderr,
	                       Map<String, String> args,
	                       String dataType,
	                       DataTable dataTable)
	{
		super(codeDef, stdout, stderr, args, dataType, null);
		this.dataTable = dataTable;
	}
}
