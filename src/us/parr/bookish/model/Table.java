package us.parr.bookish.model;

import java.util.List;

public class Table extends OutputModelObject {
	@ModelElement
	public TableRow headers;
	@ModelElement
	public List<TableRow> rows;

	public Table(TableRow headers, List<TableRow> rows) {
		this.headers = headers;
		this.rows = rows;
	}
}
