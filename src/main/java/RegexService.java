import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexService {
	public static String getVariableInFunction(String text) {
		Pattern pattern = Pattern.compile("^(int|double|void|bool|char|string)[\\s]([\\w].*)[\\(]([\\w].*)*[$\\)|\\{]");
		Matcher matcher = pattern.matcher(text);

		String variable = "";
		while (matcher.find() && !text.endsWith(";")) {
			if (matcher.groupCount() >= 3) {
				variable = matcher.group(3);
			}
		}

		return variable;
	}
	public static SimpleEntry<String, String> getStandardizedTypeVariable(String text, String variable, String replaceVariable) {
		String resultType = text;
		String resultMather = "";
		String suffixRegex = "[\\s]*(const|[\\>]|[\\)])*[\\s]*[*|&]*[\\s]*([_]*[a-zA-Z0-9].*)[;]";
		Pattern pattern = Pattern.compile(replaceVariable + suffixRegex);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find() && !text.startsWith("print")) {
			String textMatcher = matcher.group(1);
			resultType = resultType.replace(textMatcher, variable);
			resultMather = matcher.group(3);
		}
		return new SimpleEntry<String, String>(resultType, resultMather);
	}
	
	public static String getType(String text, String variable, String replaceVariable) {
		String result = text;
		Pattern pattern = Pattern.compile(replaceVariable);
		Matcher matcher = pattern.matcher(text);
//		System.out.println("TEXT = " + text);
		while (matcher.find() && !text.startsWith("print")) {
			String textMatcher = matcher.group(1);
//			System.out.println((i++) + "TEXTMATCHER = " + textMatcher);
			result = result.replaceFirst(textMatcher, variable);
		}
//		System.out.println("RESULT = " + result);
		return result;
	}
	
	public static String getNameVariable(String text) {
		Pattern pattern1 = Pattern.compile("([\\w]*)[\\s]([\\w])");
		Matcher matcher1 = pattern1.matcher(text);
		
		String result = text;
		while (matcher1.find()) {
			result = matcher1.group(2);
		}
		return result;
	}
	
	public static ArrayList<String> getValueDefine(String text) {
		ArrayList<String> define = new ArrayList<>();
		Pattern pattern = Pattern.compile("#define[\\s]+([\\w]*)[\\s]+([\\w].*)");
		Matcher matcher = pattern.matcher(text);

		// kiểm tra ở text có for hay không. Nếu có thì lấy ra rồi xóa đoạn đó đi.
		String key = "", value = "";
		while (matcher.find()) {
			if (matcher.groupCount() >= 2) {
				key = matcher.group(1);
				value = matcher.group(2);
			}
		}

		if (key != "" && value != "") {
			define.add(key);
			define.add(value);
		}

		return define;
	}

	public static ArrayList<String> getFunctionSyntax(String text) {
		ArrayList<String> lists = new ArrayList<>();
		Pattern pattern = Pattern.compile("^(int|double|void|bool|char|string)[\\s]([\\w].*)[\\(]([\\w].*)*[$\\)|\\{]");
		Matcher matcher = pattern.matcher(text);

		String typeFunction = "", nameFunction = "";
		while (matcher.find()) {
			if (matcher.groupCount() >= 2) {
				typeFunction = matcher.group(1);
				nameFunction = matcher.group(2);
			}
		}

		if (typeFunction != "" && nameFunction != "") {
			lists.add(typeFunction);
			lists.add(nameFunction);
		}

		return lists;
	}

	public static String getReturnFunction(String text) {
		String result = "";
		Pattern pattern = Pattern.compile("return[\\s]*([\\w].*)[\\;]");
		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			result = matcher.group(1);
		}

		return result;
	}
	
	public static List<String> getElementForSyntax(String text) {
		Pattern pattern = Pattern
				.compile("[;]?for[\\s]?[(][\\s]?([\\w].*)?[\\s]?[;](.*[\\s]?[\\w].*)?[\\s]?[;](.*[\\s]?)[)][\\s]?[{|\\s]*");
		Matcher matcher = pattern.matcher(text);

		List<String> result = new ArrayList<>();
		while(matcher.find()) {
			result.add(matcher.group(1));
			result.add(matcher.group(2));
			result.add(matcher.group(3));
		}
		
		return result;
	}
	
	public static SimpleEntry<String, String> getPrintSyntax(String text){
		Pattern pattern = Pattern.compile("(printf|puts)[\\(](.*)[\\)][\\;]");
		Matcher matcher = pattern.matcher(text);
		
		String key = "", value = "";
		while(matcher.find()) {
			key = matcher.group(1);
			value = matcher.group(2);
		}
		return new SimpleEntry<String, String>(key,value);
	}
	
	public static SimpleEntry<String, String> getScanSyntax(String text){
		Pattern pattern = Pattern.compile("(scanf|gets)[\\(](.*)[\\)][\\;]");
		Matcher matcher = pattern.matcher(text);
		
		String key = "", value = "";
		while(matcher.find()) {
			key = matcher.group(1);
			value = matcher.group(2);
		}
		return new SimpleEntry<String, String>(key,value);
	}
	

	public static List<String> getForSyntax(String text) {
		List<String> isList = new ArrayList<>();
		Pattern pattern = Pattern
				.compile("[;]?(for[\\s]?[(][\\s]?[\\w].*?[\\s]?[;].*[\\s]?[\\w].*?[\\s]?[;].*[\\s]?[)][\\s]?)[{|\\s]*");
		Matcher matcher = pattern.matcher(text);

		// kiểm tra ở text có for hay không. Nếu có thì lấy ra rồi xóa đoạn đó đi.
		while (matcher.find()) {
			isList.add(matcher.group(1));
		}

//		if (!isList.isEmpty()) {
//			for (String string : isList) {
//				text = text.replaceAll(string, "");
//			}
//		}
		return isList;
	}

	public static String getFileName(String path) {
		String regex = "\\\\((\\w*)((\\.cpp)|(\\.c)|(\\.h)))$";
		String result = null;

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(path);

		while (matcher.find()) {
			result = matcher.group(1);
		}

		return result;
	}

	public static boolean isAcceptFile(String path) {
		return path.matches(".*((\\.cpp)|(\\.c)|(\\.h))$");
	}

	public static String changeCodeRegex(String code) {
		String result = code.replace("(", "\\(");
		result = result.replace(")", "\\)");
		result = result.replace("+", "\\+");
		result = result.replace("*", "\\*");
		result = result.replace("^", "\\^");
		result = result.replace("}", "\\}");
		result = result.replace("{", "\\{");
		return result;
	}
}
