import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JTextArea;

public class Utility {
	public static String convertListToString(List<String> listCode) {
		StringBuilder result = new StringBuilder();
		if (listCode != null) {
			for (String code : listCode) {
				result.append(code);
			}
			return result.toString().trim();
		}
		return null;
	}

	public static String convertListToString_default(List<String> listCode) {
		StringBuilder result = new StringBuilder();
		if (listCode != null) {
			for (String code : listCode) {
				result.append(code);
				result.append("\n");
			}
			return result.toString().trim();
		}
		return null;
	}
	
	public static ArrayList<String> convertStringToArrayList(String text) {
		String s[] = text.split("\\r?\\n");
		ArrayList<String> arrList = new ArrayList<>(Arrays.asList(s));
		return arrList;
	}

	public static ArrayList<String> convertTextAreaToArrayList(JTextArea textArea) {
		String s[] = textArea.getText().split("\\r?\\n");
		ArrayList<String> arrList = new ArrayList<>(Arrays.asList(s));
		return arrList;
	}
	
	public static CodeInformation convertTextAreaToCodeInformation(JTextArea textArea, String name) {
		CodeInformation codeInfo = new CodeInformation(convertTextAreaToArrayList(textArea), null, name);
		return codeInfo;
	}

	public static int min(int x, int y, int z) {
		return Math.min(x, Math.min(y, z));
	}
	
	public static List<String> sortListZtoA(List<String> isList){
		isList.sort( new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if(o2.length() > o1.length())
					return 1;
				if(o2.length() == o1.length())
					return o2.compareToIgnoreCase(o1);
				return -1;
//				return o2.length() - o1.length();
			}
		});
		return isList;
	}
	
}
