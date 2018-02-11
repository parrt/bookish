package us.parr.bookish.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataTable {
	public List<String> colNames;
	public List<List<String>> rows = new ArrayList<>();

	public DataTable(String[] rowTexts, int firstDataRow) {
		colNames = Arrays.asList(rowTexts[firstDataRow-1].split(","));
		for (int i = firstDataRow; i<rowTexts.length; i++) {
			String[] row = rowTexts[i].split(",");
			rows.add(Arrays.asList(row));
		}
	}
}

