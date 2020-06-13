import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManagerAnalyzeText {

	public static class Singleton {
		public static final ManagerAnalyzeText instance = new ManagerAnalyzeText();
	}

	static List<String> listVariables;
	static Map<String, Set<String>> variables;

	public static ManagerAnalyzeText getInstance() {
		return Singleton.instance;
	}

	public static Map<String, Set<String>> getVariables() {
		return variables;
	}

	boolean checkCommentBlock;
	Map<String, String> mReplace;

	int numberInt, numberDouble, numberBool, numberChar, numberString;

	private ManagerAnalyzeText() {
		checkCommentBlock = false;
		numberInt = numberDouble = numberBool = numberChar = numberString = 0;

		listVariables = new LinkedList<>();
		variables = new HashMap<>();
		mReplace = ReadReplace.getReplace();
	}

	public boolean checkVoidType(String text) {
		return (text.trim().equals("void"));
	}

	public boolean checkOpenFunction(String text) {
		return (text.equals("{") || text.startsWith("{") || text.endsWith("{"));
	}

	public boolean checkCloseFunction(String text) {
		return (text.equals("}") || text.startsWith("}") || text.endsWith("}"));
	}

	public String getIndexEnd(ArrayList<String> listCode, int indexStart) {
		Stack<String> stAnalyz = new Stack<>();
		int i = indexStart;
		for (i = indexStart; i < listCode.size(); i++) {
			String code = listCode.get(i);
			if (checkCloseFunction(code) && stAnalyz.size() > 0) {
				while (stAnalyz.size() > 0 && !checkOpenFunction(stAnalyz.peek())) {
					stAnalyz.pop();
				}
				stAnalyz.pop();
				if (stAnalyz.size() == 0)
					break;
			} else if (checkCloseFunction(code) && stAnalyz.size() == 0) {
				break;
			} else {
				stAnalyz.push(code);
			}
		}
		return String.valueOf(i - 1);
	}

	public String getTrunkFunction(ArrayList<String> listCode, int indexStart, int indexEnd) {
		StringBuilder result = new StringBuilder();

		for (int i = indexStart; i <= indexEnd; i++) {
			String code = listCode.get(i);
			result.append("\n" + code);
		}
		if (result.toString().trim().isEmpty())
			return result.toString().trim();
		return result.toString().substring(1);
	}

	// nameFunction - [typeFunction - trunkFunction - indexStart - indexEnd]
	public synchronized Map<String, ArrayList<String>> getAllFunction(ArrayList<String> listCode) {
		int begin = -1, end = -1, sizeMax = listCode.size();

		Map<String, ArrayList<String>> result = new HashMap<>();
		for (int i = 0; i < sizeMax; i++) {
			String code = listCode.get(i);
			ArrayList<String> functions = RegexService.getFunctionSyntax(code);

			if (functions.size() == 2 && (code.endsWith(")") || code.endsWith("{"))) {
				String typeFunction = functions.get(0).trim();
				String nameFunction = functions.get(1).trim();
				begin = i + 1;
				if (code.endsWith("{"))
					begin--;
				end = Integer.parseInt(getIndexEnd(listCode, begin));

				String trunkFunction = getTrunkFunction(listCode, begin + 1, end);
				ArrayList<String> listTemp = new ArrayList<>();
				listTemp.add(typeFunction);
				listTemp.add(trunkFunction);
				listTemp.add(begin + "");
				listTemp.add(end + "");

				result.put(nameFunction, listTemp);

				i = end;
			}
		}
		return result;
	}

	public synchronized ArrayList<String> mergeFunctionIntoMain(ArrayList<String> listCode) {
		Map<String, ArrayList<String>> allFunctions = getAllFunction(listCode);
		Set<String> keySet = allFunctions.keySet();

		if (allFunctions.size() > 0 && allFunctions.get("main") != null) {
			String trunkMain = allFunctions.get("main").get(1);
			ArrayList<String> listTrunkMain = Utility.convertStringToArrayList(trunkMain);

			for (int i = 0; i < listTrunkMain.size(); i++) {
				String code = listTrunkMain.get(i);
				boolean isFound = false;
				for (String set : keySet) {
					if (code.contains(set)) {
						isFound = true;
						
						String codeReplaceFunction = set;
						for(int indexOfFunction = code.indexOf(set) + set.length() ; indexOfFunction < code.length() ; indexOfFunction++) {
							if(code.charAt(indexOfFunction) == ';') {
								break;
							}
							codeReplaceFunction += code.charAt(indexOfFunction);
						}
//						code = code.replace(set, allFunctions.get(set).get(1));
						code = code.replace(codeReplaceFunction, allFunctions.get(set).get(1));
					}
				}
				if (isFound) {
					listTrunkMain.set(i, code);
				}
			}
			return listTrunkMain;
		}

		return listCode;

	}

	public synchronized List<String> changeVariable(Map<String, Set<String>> isMap) {
		List<String> listResult = new ArrayList<String>();

		isMap.forEach((dataType, setVariable) -> {
			List<String> listVariable = new ArrayList<>(setVariable);
			listVariable = Utility.sortListZtoA(listVariable);

			List<String> listTmp = new LinkedList<>();
			int number = getNumber(dataType);
			for (String string : listVariable) {
				if (!isDigitSpecial(string)) {
					listTmp.add(String.valueOf(
							string + "=" + dataType.charAt(0) + dataType.charAt(dataType.length() - 1) + (number + 1)));
					number++;
				}
			}

			listResult.addAll(listTmp);
		});

		return listResult;
	}

	public int checkCommentBlock(char characIndex, char characIndexNext, int index, int sizeOfText,
			boolean flagComment) {
		// 1 to comment open ( /* ) - 2 to comment close ( */ )
		if (index < sizeOfText - 1 && characIndex == '/' && characIndexNext == '*')
			return 1;
		if (index < sizeOfText - 1 && characIndex == '*' && characIndexNext == '/' && flagComment)
			return 2;
		return 0;
	}

	private boolean checkDefine(String text) {
		return (text.startsWith("#define"));
	}

	public boolean checkNotRead(String text) {
		if (text.trim().startsWith("//") || text.trim().startsWith("#include") || text.trim().startsWith("using name")
				|| text.trim().startsWith("assert") || text.trim().equals(";") || checkFastIO(text))
			return true;

		return false;
	}

	public boolean checkScan(String text) {
		if (text.trim().startsWith("scanf") || text.trim().startsWith("print") || text.trim().startsWith("get"))
			return true;
		return false;
	}

	public boolean checkPrint(String text) {
		if (text.trim().startsWith("count") || text.trim().startsWith("print") || text.trim().startsWith("put"))
			return true;
		return false;
	}

	public boolean checkFastIO(String text) {
		if (text.trim().startsWith("ios::") || text.trim().startsWith("ios_base::") || text.trim().startsWith("cin.tie")
				|| text.trim().startsWith("cout.tie"))
			return true;
		return false;
	}

	private StringBuilder clearStringBuilder(StringBuilder text) {
		return (text.delete(0, text.length()));
	}

	public synchronized List<String> formatAllCode(List<String> listCode, int begin, int end, boolean isFormated) {

		List<String> listResult = new ArrayList<>();
		if (!isFormated) {
			List<String> listFormat = new ArrayList<>();
			for (String code : listCode) {
				SimpleEntry<List<String>, Boolean> entryTemp = formatCode(code);

				listFormat.addAll(entryTemp.getKey());
			}
			listCode.clear();
			listCode.addAll(listFormat);

			end = listCode.size();
		}

		for (int i = begin; i < Math.min(listCode.size(), end); i++) {
			String code = listCode.get(i).trim();
			SimpleEntry<List<String>, Boolean> entryTemp = formatCode(code);

			List<String> listTemp = entryTemp.getKey();
			boolean isForSyntax = entryTemp.getValue();

			if (isForSyntax) {
				// System.out.println("***************TEMP*************");

				SimpleEntry<List<String>, Integer> entryNewLoop = getNewLoop(listCode, listTemp, i);
				listTemp = entryNewLoop.getKey();
				i = entryNewLoop.getValue();
			}

			SimpleEntry<String, String> entryCout;
			if (isCout(code)) {
				for (int index = 0; index < listTemp.size(); index++) {
					String codeTemp = listTemp.get(index);
					entryCout = RegexService.getPrintSyntax(codeTemp.trim());
					String key = entryCout.getKey();
					String value = entryCout.getValue();
					if (!key.equals("") && !value.equals("")) {
						String newSyntax = "cout";
						if (key.equals("printf")) {
							String textCout = value.substring(0, value.lastIndexOf('\"') + 1);
							newSyntax += "<<" + textCout;
							if (value.length() > (value.lastIndexOf('\"') + 2)) {
								String variableCout = value.substring(value.lastIndexOf('\"') + 2);

								String variableCouts[] = variableCout.trim().split(",");

//								String beginCout = "\"<<";
//								String endCout = "<<\"";
								for (String variable : variableCouts) {
									if (!variable.trim().equals("")) {
										String[] splitsVariable = newSyntax.split("%");

										if (splitsVariable.length > 1) {
											
											int sizeVariable =splitsVariable[1].length();
											
											if(splitsVariable[1].contains("d")) {
												sizeVariable = Math.min(sizeVariable, splitsVariable[1].indexOf("d") + 1);
											}else if(splitsVariable[1].contains("f")) {
												sizeVariable = Math.min(sizeVariable, splitsVariable[1].indexOf("f") + 1);
											}else if(splitsVariable[1].contains("s")) {
												sizeVariable = Math.min(sizeVariable, splitsVariable[1].indexOf("s") + 1);
											}else if(splitsVariable[1].contains("c")) {
												sizeVariable = Math.min(sizeVariable, splitsVariable[1].indexOf("c") + 1);
											}
											String regexReplaceVariable = "%.{1," + sizeVariable + "}";
//											String valueReplace = "\"<<" + variable;
											String valueReplace = "\"<<" + variable + "<<\"";
											
											String tempSyntax = newSyntax;
											tempSyntax = tempSyntax.replaceFirst(regexReplaceVariable, valueReplace);
											
//											if(tempSyntax.contains("%")) {
//												valueReplace = "\"<<" + variable + "<<\"";
////												valueReplace = "\"<<\"" + valueReplace;
//											}
											
											newSyntax = newSyntax.replaceFirst(regexReplaceVariable, valueReplace);
										}
//										newSyntax = newSyntax.replaceFirst("%.{1,3}", beginCout + variable + endCout);
									}
								}
							}
							newSyntax += ";";

						} else {
							newSyntax += ("<< " + value + ";");
						}
						listTemp.set(index, newSyntax);
					}
				}

			} else if (isCin(code)) {

				for (int index = 0; index < listTemp.size(); index++) {
					String codeTemp = listTemp.get(index);
					entryCout = RegexService.getScanSyntax(codeTemp.trim());
					String key = entryCout.getKey();
					String value = entryCout.getValue();
					if (!key.equals("") && !value.equals("")) {
						String newSyntax = "cin";
						if (key.equals("scanf")) {
							String textCout = value.substring(0, value.lastIndexOf('\"') + 1);
							textCout = textCout.replace("\"", "");
							newSyntax += textCout;
							if (value.contains("\"") && value.length() >= (value.lastIndexOf('\"') + 2)) {
								String variableCout = value.substring(value.lastIndexOf('\"') + 2);

								String variableCouts[] = variableCout.trim().split(",");

								for (String variable : variableCouts) {
									if (!variable.trim().equals("")) {
										variable = variable.replace("&", "");
										// %.{1,3}
										String[] splitsVariable = newSyntax.split("%");

										if (splitsVariable.length > 1) {

											String regexReplaceVariable = "%.{1," + splitsVariable[1].length() + "}";
											newSyntax = newSyntax.replaceFirst(regexReplaceVariable, ">>" + variable);
										}
									}
								}
							}
							newSyntax += ";";
						} else {
							newSyntax += (">>" + value + ";");
						}
						listTemp.set(index, newSyntax);
					}
				}
			}

			listResult.addAll(listTemp);

		}

		return listResult;
	}

	public boolean isCin(String text) {
		return (text.contains("scanf") || text.contains("gets"));
	}

	public boolean isCout(String text) {
		return (text.contains("printf") || text.contains("puts"));
	}

	private SimpleEntry<List<String>, Integer> getNewLoop(List<String> listCode, List<String> listTemp, int i) {
		int j = i + 1;

		List<String> convertForToWhile = RegexService.getElementForSyntax(listTemp.get(0).trim());
		List<String> listTrunkFor = new ArrayList<>();
		int beginFor = i, endFor = i;
		Stack<String> stFor = new Stack<>();

		/* GET INDEX BEGIN - END FOR SYNTAX */
		for (String codeTemp : listTemp) {
			if (checkOpenFunction(codeTemp.trim()) && stFor.size() == 0) {
				stFor.add(codeTemp);
				listTrunkFor.add(codeTemp);
				beginFor = i + 1;
				continue;
			}
			if (listTrunkFor.size() > 0)
				listTrunkFor.add(codeTemp);

		}

		String codeNext = listCode.get(j);
		if (checkOpenFunction(codeNext.trim()) && stFor.size() == 0) {
			listTrunkFor.add(codeNext);
			stFor.add(codeNext);
			beginFor = j + 1;
		}

		/* GET TRUNK FOR SYNTAX */
		if (beginFor != i) {
			endFor = getIndexEndFor(listCode, stFor, beginFor);
			listTrunkFor.addAll(getTrunkForSyntax(listCode, beginFor, endFor));
		}
		if (beginFor == i) {
			endFor = i;
			listTrunkFor.add("{");
			for (int index = 1; index < listTemp.size(); index++)
				listTrunkFor.add(listTemp.get(index));

			if (listTrunkFor.size() == 1) {
				beginFor++;
				endFor++;
				listTrunkFor.addAll(getTrunkForSyntax(listCode, beginFor, endFor));
				listTrunkFor.add("}");
			}
		}

		/* CONVERT FOR TO WHILE */
		String initFor = convertForToWhile.get(0) + ";";
		String conditionFor = convertForToWhile.get(1);
		String stepFor = convertForToWhile.get(2) + ";";

		listTrunkFor.add(0, "while(" + conditionFor + ")");
		listTrunkFor.add(0, initFor);
		String replaceLast = listTrunkFor.get(listTrunkFor.size() - 1);
		replaceLast = replaceLast.replace("}", stepFor + "}");
		listTrunkFor.set(listTrunkFor.size() - 1, replaceLast);
//		System.out.println(listTrunkFor);
//		System.out.println("******************");
//		listResult.addAll(listTrunkFor);

		return new SimpleEntry<List<String>, Integer>(listTrunkFor, endFor);
	}

	private List<String> getTrunkForSyntax(List<String> listCode, int indexBegin, int indexEnd) {
		List<String> result = new ArrayList<>();

		result.addAll(formatAllCode(listCode, indexBegin, indexEnd + 1, true));

		return result;
	}

	private int getIndexEndFor(List<String> listCode, Stack<String> stFor, int indexBegin) {
		int i = indexBegin;
		for (i = indexBegin; i < listCode.size(); i++) {
			String code = listCode.get(i);
			if (checkCloseFunction(code) && stFor.size() > 0) {
				while (stFor.size() > 0 && !checkOpenFunction(stFor.peek())) {
					stFor.pop();
				}
				stFor.pop();
				if (stFor.size() == 0)
					break;
			} else if (checkCloseFunction(code) && stFor.size() == 0) {
				break;
			} else {
				stFor.push(code);
			}
		}
		return i;
	}

	public synchronized List<String> formatAllVariable(List<String> listCode) {
		List<String> listResult = new ArrayList<>();

		for (String code : listCode) {
			listResult.add(formatVariable(listVariables, code));
		}

		return listResult;
	}

	private SimpleEntry<List<String>, Boolean> formatCode(String text) {
		List<String> listResult = new ArrayList<>();
		boolean isForSyntax = false;
		text = text.trim();
		if (checkDefine(text)) {
			listResult.add(text);
			return new SimpleEntry<>(listResult, isForSyntax);
		}
		if (text.startsWith("//")) {
			return new SimpleEntry<>(listResult, isForSyntax);
		}
		text = text.replace("\n", "").trim();

		if (!checkCommentBlock)
			listResult.addAll(RegexService.getForSyntax(text));
		/* */
		if (!listResult.isEmpty()) {
			for (String string : listResult) {
				text = text.replace(string, "");
			}
			isForSyntax = true;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {

			if (i < text.length() - 1 && checkCommentBlock(text.charAt(i), text.charAt(i + 1), i, text.length(),
					checkCommentBlock) == 1) {
				checkCommentBlock = true;
			}
			if (i < text.length() - 1 && checkCommentBlock(text.charAt(i), text.charAt(i + 1), i, text.length(),
					checkCommentBlock) == 2) {
				checkCommentBlock = false;
				i++;
				continue;
			}
			if (!checkCommentBlock) {
				if ((text.charAt(i) == ';' || text.charAt(i) == '{' || text.charAt(i) == '}')
						&& notEmptyStringBuilder(sb)) {
					if (sb.toString().contains("//")) {
						String tmp = sb.toString();
						sb = clearStringBuilder(sb);
						sb.append(tmp.substring(0, tmp.toString().indexOf("//")));
					}

					if (!sb.toString().isEmpty() && !checkNotRead(sb.toString().trim())) {
						listResult.add(sb.toString().trim() + text.charAt(i));
					}
					sb = clearStringBuilder(sb);
					continue;
				} else {
					if ((text.charAt(i) == '}' || text.charAt(i) == '{') && notEmptyStringBuilder(sb)) {
						String tmp = sb.toString();
						listResult.add(String.valueOf(text.charAt(i)));
						sb = clearStringBuilder(sb);
						sb.append(tmp.substring(0, tmp.toString().indexOf(text.charAt(i))));

					} else if ((text.charAt(i) == '}' || text.charAt(i) == '{')) {
						listResult.add(String.valueOf(text.charAt(i)));
					} else {
						sb.append(text.charAt(i));
					}
				}

			}

		}
		if (!checkCommentBlock && notEmptyStringBuilder(sb) && !checkNotRead(sb.toString().trim())) {
			if (sb.toString().contains("//")) {
				String tmp = sb.toString();
				sb = clearStringBuilder(sb);
				sb.append(tmp.substring(0, tmp.toString().indexOf("//")));
			}

			if (!sb.toString().isEmpty() && !checkNotRead(sb.toString().trim())) {
				listResult.add(sb.toString().trim());
			}
			sb = clearStringBuilder(sb);
		}

		return new SimpleEntry<>(listResult, isForSyntax);
	}

	private synchronized String formatVariable(List<String> listVariables, String text) {
		String result = text;
		for (String variables : listVariables) {
			String split[] = variables.split("=");
			String oldChar = split[0].trim();
			String newChar = split[1].trim();

			int indexOld = 0;
			int index = result.indexOf(oldChar, indexOld);
			while (index != -1) {
				if (index == 0 && isDigitSpecial(result.charAt(index + oldChar.length()) + "")) {
					String tmpSt = result.substring(0, index);
					String tmpEn = result.substring(index + oldChar.length(), result.length());
					result = tmpSt + newChar + tmpEn;
				} else if (index > 0 && index < result.length() - 1 && isDigitSpecial(result.charAt(index - 1) + "")
						&& isDigitSpecial(result.charAt(index + oldChar.length()) + "")) {
					String tmpSt = result.substring(0, index);
					String tmpEn = result.substring(index + oldChar.length(), result.length());
					result = tmpSt + newChar + tmpEn;

				}
				index = result.indexOf(oldChar, indexOld);
				indexOld = index + 1;
			}
		}
		return result;
	}

	public synchronized Map<String, String> getAllDefine(List<String> listCode) {
		Map<String, String> mResult = new HashMap<>();

		listCode.forEach(code -> {
			mResult.putAll(getDefineSyntax(code));
		});
//		System.out.println(mResult);
		return mResult;
	}

	private Map<String, String> getDefineSyntax(String text) {
		Map<String, String> mResult = new HashMap<>();
		if (!text.startsWith("#define"))
			return mResult;

		ArrayList<String> define = RegexService.getValueDefine(text);
		if (define.size() >= 2)
			mResult.put(define.get(0), define.get(1));

		return mResult;
	}

	private int getNumber(String variable) {
		if (variable.equals("bool"))
			return numberBool;
		if (variable.equals("int"))
			return numberInt;
		if (variable.equals("double"))
			return numberDouble;
		if (variable.equals("char"))
			return numberChar;
		if (variable.equals("string"))
			return numberString;
		return -1;
	}

	public synchronized List<String> ignoreDigitSpecial(List<String> listCode) {
		List<String> isList = new ArrayList<>();
//		StringBuilder res = new StringBuilder();
		for (String code : listCode) {
			if (code.startsWith("ios::") || code.startsWith("ios_base::"))
				continue;
			code = code.replaceAll("\\W", "");
			if (!code.equals("")) {
				isList.add(code);
//				res.append(code);
			}
		}
		variables.clear();
		listVariables.clear();
		checkCommentBlock = false;
		numberInt = numberDouble = numberBool = numberChar = numberString = 0;
		return isList;
//		return res.toString();
	}

	public boolean isDigitSpecial(String text) {
		String regex = ("\\W");
		String newChar = "==>";
		text = text.replaceAll(regex, newChar);
		return (text.contains(newChar));
	}

	public boolean isPunctuation(char c) {
		return (c == '(' || c == ')' || c == '.' || c == ',' || c == ';' || c == '?' || c == '<' || c == '>' || c == '&'
				|| c == ' ');
	}

	public boolean notEmptyStringBuilder(StringBuilder text) {
		return (text.toString().trim().length() != 0);
	}

	public synchronized List<String> replaceAllCode(List<String> listCode) {
		List<String> isList = new ArrayList<>();
		for (String code : listCode) {
			code = replaceFunction(code);
			isList.add(replaceVariable(code));
		}
		actionReplaceVariable();
		return isList;
	}

	public synchronized void actionReplaceVariable() {
		listVariables = changeVariable(variables);
		listVariables = Utility.sortListZtoA(listVariables);
		// System.out.println(listVariables);

	}

	public synchronized List<String> replaceAllDefineToCode(Map<String, String> isMap, List<String> listCode) {
		List<String> listResult = new ArrayList<>();

		listCode.forEach(code -> {
			String text = replaceDefineToCode(isMap, code);
			if (text != null) {
				listResult.add(text);
			}
		});

		return listResult;
	}

	private String replaceDefineToCode(Map<String, String> isMap, String code) {
		if (code.startsWith("#define") || code.startsWith("get") || code.startsWith("put"))
			return null;
		String result = code;
		Set<String> keySet = isMap.keySet();
		if (code.startsWith("int main"))
			return result;
		for (String defineValue : keySet) {
			String oldChar = defineValue;
			String newChar = isMap.get(defineValue);

//			System.out.println("OLDCHAR = " + oldChar);
//			System.out.println("NEWCHAR = " + newChar);
			String regex = "([^\\w\\d]" + RegexService.changeCodeRegex(oldChar) + "[^\\w\\d])";
			if (oldChar.trim().equals(code.trim())) {
				result = newChar;
			} else if (code.trim().startsWith(oldChar.trim())) {
				regex = "(" + RegexService.changeCodeRegex(oldChar) + "\\W*)";
				result = result.replaceFirst(regex, newChar + " ");
			} else if (code.contains(oldChar)) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(code);
				String textTail = "", textHead = "";
				while (matcher.find()) {
					String tmp = matcher.group(1).toString();
					textHead = String.valueOf(tmp.charAt(0) + "");

					textTail = String.valueOf(tmp.charAt(tmp.length() - 1) + "");

					if (isOpen(textHead)) {
						if (!isClose(textTail)) {
							textTail = "]" + textTail;
						}
					}
					textTail = textTail.replace("\\", "\\\\");
					textHead = textHead.replace("\\", "\\\\");
					result = result.replaceFirst(regex, textHead + newChar + textTail);
//					System.out.println("AFTER = " + result);
				}
			}
			code = result;
		}

		return result;
	}

	private boolean isClose(String text) {
		return (text.equals("]"));
	}

	private boolean isOpen(String text) {
		return (text.equals("["));
	}

	public List<String> replaceSpaceAfterLetterEqual(List<String> isList) {
		List<String> listResult = new ArrayList<>();

		String regex = "([\\s]*[\\W][\\s]*)";

		for (String code : isList) {
			code = code.replaceAll("\\?", "");
			code = code.replaceAll("\\\\n", "");
			if (isDigitSpecial(code)) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(code);

				while (matcher.find()) {
					String tmp1 = matcher.group(1);
					String tmp = tmp1.trim();
					if (!tmp.equals(" ") && !tmp.equals("") && !tmp1.equals(tmp)) {

						tmp1 = RegexService.changeCodeRegex(tmp1);
						tmp = RegexService.changeCodeRegex(tmp);
						code = code.replaceFirst(tmp1, tmp);
					}

				}
				listResult.add(code);

			} else
				listResult.add(code);
		}
		return listResult;
	}

	private synchronized String replaceFunction(String text) {
		String result = text.trim();
		Set<String> ketSet = mReplace.keySet();
		for (String variable : ketSet) {
			result = RegexService.getType(result, variable, mReplace.get(variable));
		}
		return result;
	}

	private synchronized String replaceVariable(String text) {
		String result = text.trim();
		Set<String> ketSet = mReplace.keySet();
		Set<String> listVariable = new HashSet<String>();
		String keyTmp = "";
//		System.out.println("TEXT = " + text);
		for (String variable : ketSet) {
//			System.out.println("VARIABLE = " + variable);

			SimpleEntry<String, String> entryResult = RegexService.getStandardizedTypeVariable(result, variable,
					mReplace.get(variable).trim());
			String value = entryResult.getValue();
			if (!value.equals("")) {

				result = entryResult.getKey();
				keyTmp = variable;
//				System.out.println("RESULT_REPLACE = " + result);
//				System.out.println("VALUE = " + value);
				String split[] = value.split(",");

				if (split.length > 0) {
					for (String string : split) {
						String tmpSplit[] = string.trim().split("=");

						if (tmpSplit[0].trim().contains("[")) {
							String tmpSplit1[] = tmpSplit[0].trim().split("\\[");
							tmpSplit[0] = tmpSplit1[0].trim();
						}
						String variableName = RegexService.getNameVariable(tmpSplit[0].trim());
//						System.out.println("VARIABLE_NAME = " + variableName);
						listVariable.add(variableName);
					}
				}
			} else {
				String textTemp = RegexService.getVariableInFunction(result);
				if (textTemp != null && !textTemp.equals("")) {
					keyTmp = variable;
					String split[] = textTemp.split(",");
					if (split.length > 0) {
						for (String string : split) {
							String tmpSplit[] = string.trim().split("=");

							if (tmpSplit[0].trim().contains("[")) {
								String tmpSplit1[] = tmpSplit[0].trim().split("\\[");
								tmpSplit[0] = tmpSplit1[0].trim();
							}
							String variableName = RegexService.getNameVariable(tmpSplit[0].trim());
							listVariable.add(variableName);
						}
					}
				}
			}
		}

		final String tmp = keyTmp;
		variables.forEach((key, val) -> {
			if (key.equals(tmp)) {
				listVariable.addAll(val);
			}
		});
		variables.put(tmp, listVariable);
		return result;
	}
}
