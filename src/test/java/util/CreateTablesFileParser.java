package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CreateTablesFileParser {
	URL file;
	String filePath;
	
	public CreateTablesFileParser(URL file) {
		this.file = file;
		this.filePath = file.getPath();
	}
	
	public String[] parse() throws IOException {
		String lineSep = System.getProperty("line.separator");
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = null;
		List<String> sqls = new ArrayList<String>();
		String sql = "";
		while((line = br.readLine()) != null) {
			if(line.trim().equals("")){
				sqls.add(sql);
				sql = "";				
			} else {
				sql += line;
			}
		}
		String sqlsArr[] = new String[sqls.size()];
		sqls.toArray(sqlsArr);
		return sqlsArr;
	}
}
