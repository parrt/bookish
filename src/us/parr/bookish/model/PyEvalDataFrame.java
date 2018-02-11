package us.parr.bookish.model;

import us.parr.bookish.model.entity.ExecutableCodeDef;
import us.parr.bookish.util.DataTable;

public class PyEvalDataFrame extends PyEval {
	public DataTable dataTable;

	public PyEvalDataFrame(ExecutableCodeDef codeDef,
	                       String stdout,
	                       String stderr,
	                       String dataType,
	                       DataTable dataTable)
	{
		super(codeDef, stdout, stderr, dataType, null);
		this.dataTable = dataTable;
	}
}
