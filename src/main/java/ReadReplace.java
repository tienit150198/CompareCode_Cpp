import java.util.Map;

public class ReadReplace {
	static Map<String, String> replace;
	static {
		initialization();
	}

	protected static Map<String, String> getReplace() {
		return replace;
	}

	private static void initialization() {
		replace = new FileService().readReplace("files/replace.dat", "->");
	}

	protected static void setReplace(Map<String, String> replace) {
		ReadReplace.replace = replace;
	}

}
