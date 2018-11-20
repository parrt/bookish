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
			Set<String> colNames = parser.getHeaderMap().keySet();
			this.colNames.addAll(colNames);
			this.firstColIsIndex = true;
			for (CSVRecord record : parser) {
				List<String> row = new ArrayList<>();
				for (int i = 0; i<record.size(); i++) {
					String v = record.get(i);
					boolean isInt = false;
					try {
						Integer.parseInt(v);
						isInt = true;
					}
					catch (NumberFormatException nfe) {
						isInt = false;
					}
					if ( !isInt && !NumberUtils.isDigits(v) && NumberUtils.isCreatable(v) ) {
						v = String.format("%.4f",Precision.round(Double.valueOf(v), 4));
					}
					else {
						v = abbrevString(v, 25);
					}
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

