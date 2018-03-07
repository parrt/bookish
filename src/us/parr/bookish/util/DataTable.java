package us.parr.bookish.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Precision;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static us.parr.lib.ParrtStrings.abbrevString;

public class DataTable {
	public List<String> colNames = new ArrayList<>();
	public List<List<String>> rows = new ArrayList<>();
	public boolean firstColIsIndex;

	public DataTable(String csv) {//, int firstDataRow) {
		parseCSV(csv);
	}

	public void parseCSV(String csv) {
		try {
			Reader in = new StringReader(csv);
			CSVFormat format = CSVFormat.EXCEL.withHeader();
			CSVParser parser = format.parse(in);
			this.firstColIsIndex = false;
			for (CSVRecord record : parser) {
				if ( !firstColIsIndex && Character.isAlphabetic(record.get(0).charAt(0)) ) {
					// latch if we see alpha not number
					firstColIsIndex = true;
				}
				List<String> row = new ArrayList<>();
				for (int i = 0; i<record.size(); i++) {
					String v = record.get(i);
					if ( !NumberUtils.isDigits(v) && NumberUtils.isCreatable(v) ) {
						v = String.format("%.4f",Precision.round(Double.valueOf(v), 4));
					}
					else {
						v = abbrevString(v, 25);
					}
					row.add(v);
				}
				rows.add(row);
			}
			Set<String> colNames = parser.getHeaderMap().keySet();
			if ( !firstColIsIndex ) {
				colNames.remove(""); // remove index column name
			}
			this.colNames.addAll(colNames);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

