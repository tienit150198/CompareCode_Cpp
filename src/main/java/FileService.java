import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileService {
	public List<String> readCodeWithPath(String path) {
		File file = new File(path);
		ArrayList<String> listCode = new ArrayList<>();

		if (!file.exists()) {
			System.out.println("File not found, please create file");
			return null;
		}

		try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
			String readLine;
			while ((readLine = bfr.readLine()) != null) {
				listCode.add(readLine);
			}

			bfr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listCode;
	}

	public CodeInformation getCodeInfor(String path) {
		ArrayList<String> listCurrentCode = (ArrayList<String>) readCodeWithPath(path);
		CodeInformation info = null;
		String fileName = RegexService.getFileName(path);
		if (listCurrentCode != null && !listCurrentCode.isEmpty()) {
			info = new CodeInformation(listCurrentCode, null, fileName, path);
		}
		return info;
	}

	public List<CodeInformation> readCodeInfolder(File folder) {
		ArrayList<CodeInformation> allCode = new ArrayList<>();

		if (!folder.exists()) {
			System.out.println("Folder not found, please change path folder");
			return null;
		}
		for (final File fileEntry : folder.listFiles()) {
			String path = fileEntry.getAbsolutePath();
			if (RegexService.isAcceptFile(path)) {
				CodeInformation info = getCodeInfor(path);
				if (info != null) {
					allCode.add(info);
				}
			}
		}

		return allCode;
	}

	public Map<String, String> readReplace(String path, String format) {
		Map<String, String> isMap = new LinkedHashMap<>();
		File file = new File(path);

		if (!file.exists()) {
			System.out.println("File not found, please create file");
			return null;
		}

		try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
			String readLine;
			while ((readLine = bfr.readLine()) != null) {
				if (readLine.trim().startsWith("//") || readLine.trim().startsWith("--") || readLine.trim().equals(""))
					continue;

				String split[] = readLine.split(format);
				isMap.put(split[0].trim(), split[1].trim());
			}

			bfr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isMap;
	}
}
