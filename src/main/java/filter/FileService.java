package filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FileService {
	public static Map<String, String> readConfig(File file, String format) {
		try {
			Map<String, String> isMap = new HashMap<>();
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bfr = new BufferedReader(isr);

			String readLine = null;
			while ((readLine = bfr.readLine()) != null) {
				if (readLine.startsWith("--") && readLine.endsWith("--"))
					continue;

				String split[] = readLine.split(format);

				isMap.put(split[0].trim(), split[1].trim());
			}

			bfr.close();
			isr.close();
			fis.close();
			return isMap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
