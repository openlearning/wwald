package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KvTableFileParser {

	private URL url;

	public static class KvTableItem {
		public final String k;
		public final String v;

		public KvTableItem(String k, String v) {
			this.k = k;
			this.v = v;
		}
	}

	public KvTableFileParser(URL url) {
		this.url = url;
	}

	public KvTableItem[] parse() throws IOException {
		List<KvTableItem> kvTableItemsList = new ArrayList<KvTableItem>();
		String path = url.getPath();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = null;
		while((line = reader.readLine()) != null) {
			String kv[] = line.split("=");
			kvTableItemsList.add(new KvTableItem(kv[0], kv[1]));
		}
		KvTableItem retVal[] = new KvTableItem[kvTableItemsList.size()];
		retVal = kvTableItemsList.toArray(retVal);
		return retVal;
	}
}
