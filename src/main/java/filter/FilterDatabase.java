package filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class FilterDatabase {
	private static final String FILES_OLD_URL = "files/data.dat";
	
	static Map<String,String> mData = new HashMap<>();
	
	private static void setUpdate_OldUrl() {
		File file = new File(FILES_OLD_URL);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, true);
			PrintStream ps_oldUrl = new PrintStream(fos);
			System.setOut(ps_oldUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println("Crawl.setUpdate_OldUrl done");
	}
	private static void getDataFromDatabase() {
		Connect connect = new Connect();
		mData.putAll(connect.getAllData());
		System.err.println("FilterDatabase.getDataFromDatabase()");
	}
	
	private static boolean isIgnore(String text) {
		if(text.contains("#endif") || text.contains("#ifndef") || text.contains("typedef")
				|| text.contains("struct") || text.contains("c_str") || text.contains("pragma ") 
				|| text.contains("INT.MAX") || text.contains("int64_t") || text.contains("auto")
				|| text.contains("iterator") || text.contains("inline") || text.contains("friend")
				|| text.contains("class") || text.contains("max_element"))
			return true;
		return false;
	}
	
	private static void filterData() {
		final Connect connect = new Connect();
		mData.forEach((link, code)->{
			if(!isIgnore(code) && code.contains("int main(")) {
				String _link = link.substring(0,link.lastIndexOf("/"));
				String problem = _link.substring(_link.lastIndexOf("/"), _link.length());
				_link = _link.substring(0, _link.lastIndexOf("/"));
				String sufLink = link.substring(link.lastIndexOf("/"), link.length());
				
//				connect.Insert_codeData(_link, problem , sufLink, code);
				System.out.println(_link + " -> " + problem + " -> " + sufLink);
//				System.out.println(link + "=====>>>>>" + code);
			}
		});
	}
	
	public static void main(String[] args) {
		setUpdate_OldUrl();
		getDataFromDatabase();
		filterData();
	}
}
