import java.util.ArrayList;

public class CodeInformation {
	static int id;

	static int getId() {
		return id;
	}

	static void setId(int id) {
		CodeInformation.id = id;
	}

	String fileName, path;
	ArrayList<String> listCode;
	ArrayList<String> stepCode;

	public CodeInformation() {
		super();
		id++;
	}

	public CodeInformation(ArrayList<String> listCode, ArrayList<String> stepCode, String fileName) {
		this();
		this.listCode = listCode;
		this.stepCode = stepCode;
		this.fileName = fileName;
	}

	public CodeInformation(ArrayList<String> listCode, ArrayList<String> stepCode, String fileName, String path) {
		this();
		this.listCode = listCode;
		this.stepCode = stepCode;
		this.fileName = fileName;
		this.path = path;
	}

	String getFileName() {
		return fileName;
	}

	ArrayList<String> getListCode() {
		return listCode;
	}

	String getPath() {
		return path;
	}

	ArrayList<String> getStepCode() {
		return stepCode;
	}

	void setFileName(String fileName) {
		this.fileName = fileName;
	}

	void setListCode(ArrayList<String> listCode) {
		this.listCode = listCode;
	}

	void setPath(String path) {
		this.path = path;
	}

	void setStepCode(ArrayList<String> stepCode) {
		this.stepCode = stepCode;
	}

	@Override
	public String toString() {
		return "CodeInformation [listCode=" + listCode + ", stepCode=" + stepCode + ", fileName=" + fileName + "]";
	}

}
