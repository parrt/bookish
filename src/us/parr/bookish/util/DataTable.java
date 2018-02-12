package us.parr.bookish.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static us.parr.lib.ParrtStrings.abbrevString;

public class DataTable {
	public List<String> colNames = new ArrayList<>();
	public List<List<String>> rows = new ArrayList<>();

	public DataTable(String csv) {//, int firstDataRow) {
		parseCSV(csv);
	}

	public void parseCSV(String csv) {
		try {
			Reader in = new StringReader(csv);
			CSVFormat format = CSVFormat.EXCEL.withHeader();
			CSVParser parser = format.parse(in);
			colNames.addAll(parser.getHeaderMap().keySet());
			for (CSVRecord record : parser) {
				List<String> row = new ArrayList<>();
				for (int i = 0; i<record.size(); i++) {
					String v = record.get(i);
					v = abbrevString(v, 45);
					row.add(v);
				}
				rows.add(row);
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

