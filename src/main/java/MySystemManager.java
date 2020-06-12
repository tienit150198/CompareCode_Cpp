import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MySystemManager {
	static Map<Integer, List<String>> sameList = new HashMap<>();
	
	private static Thread threadClassify;
	public static double ratio = 0.0;

	private static Classify c;

	public MySystemManager() {
		super();
		ratio = 0.0;
	}

	public static class SystemRunningCompute implements Callable<Double> {
		private ManagerAlgorithm instanceAlgorithm;
		
		public static final int EDIT_DISTANCE = 1;
		public static final int BRUTE_FORCES = 2;
		public static final int Z_FUNCTION = 3;
		public static final int SUFFIX_ARRAY = 4;
		List<String> listCode1, listCode2;
		int algorithmType = 1;

		public SystemRunningCompute(List<String> listCode1, List<String> listCode2, int alrogithmType) {
			instanceAlgorithm = ManagerAlgorithm.getInstance();
			this.listCode1 = listCode1;
			this.listCode2 = listCode2;
			this.algorithmType = alrogithmType;
		}

		private synchronized double computeUseEditDistance(List<String> listCode1, List<String> listCode2) {
			String textCode1 = Utility.convertListToString(listCode1);
			String textCode2 = Utility.convertListToString(listCode2);
//			System.out.println(textCode1);
//			System.out.println(textCode2);
			if (textCode1.length() < textCode2.length()) {
				String textTmp = textCode1;
				textCode1 = textCode2;
				textCode2 = textTmp;
			}

			int sizeOfTextCode1 = textCode1.length();
			int sizeOfTextCode2 = textCode2.length();

			int numberEditdistance = instanceAlgorithm.editDistDP(textCode1, textCode2, sizeOfTextCode1,
					sizeOfTextCode2);
//			System.out.println(numberEditdistance);
//			System.out.println(
//					"numberEditdistance = " + numberEditdistance + " - " + sizeOfTextCode1 + " : " + sizeOfTextCode2);
			return (1 - (numberEditdistance / (double) Math.max(sizeOfTextCode1, sizeOfTextCode2))) * 100;
		}

		private synchronized double computeZfunction(List<String> listCode1, List<String> listCode2) {
			String textCode1 = Utility.convertListToString(listCode1);
			String textCode2 = Utility.convertListToString(listCode2);
//			System.out.println(textCode1);
//			System.out.println(textCode2);
			if (textCode1.length() < textCode2.length()) {
				String textTmp = textCode1;
				textCode1 = textCode2;
				textCode2 = textTmp;
			}

			int sizeOfTextCode1 = textCode1.length();
			int sizeOfTextCode2 = textCode2.length();

			int numberZSearch = instanceAlgorithm.ZSearch(textCode1, textCode2);
//			System.out.println(numberEditdistance);
//			System.out.println("numberEditdistance = " + numberEditdistance);
			return Math.max((Math.max(sizeOfTextCode1 - numberZSearch, 1) / (double) sizeOfTextCode2) * 100, 0.0);
		}

		private synchronized double computeBruteForces(List<String> listCode1, List<String> listCode2) {
			String textCode1 = Utility.convertListToString(listCode1);
			String textCode2 = Utility.convertListToString(listCode2);
			if (textCode1.length() < textCode2.length()) {
				String textTmp = textCode1;
				textCode1 = textCode2;
				textCode2 = textTmp;
			}

			int sizeOfTextCode1 = textCode1.length();
			int sizeOfTextCode2 = textCode2.length();

			int maxCharacterSame = instanceAlgorithm.bruteForceCompare(textCode1.toLowerCase(), textCode2.toLowerCase());
			System.out.println("SIZE = " + sizeOfTextCode1 + " - " + sizeOfTextCode2);
			System.out.println("SAME MAX = " + maxCharacterSame);
			System.out.println("RATIO = " + (maxCharacterSame * 2) / (sizeOfTextCode1 + sizeOfTextCode2) * 100.0);
			
			return Math.max((maxCharacterSame * 2) / (double)(sizeOfTextCode1 + sizeOfTextCode2) * 100.0, 0.0);
		}

		private synchronized double computeSuffixArray(List<String> listCode1, List<String> listCode2) {
			String textCode1 = Utility.convertListToString(listCode1);
			String textCode2 = Utility.convertListToString(listCode2);

			if (textCode1.length() < textCode2.length()) {
				String textTmp = textCode1;
				textCode1 = textCode2;
				textCode2 = textTmp;
			}

		//	System.out.println(textCode1.length() + " - " + textCode2.length());
			String text = textCode1.toLowerCase() + "$" + textCode2.toLowerCase() + "#";
			
			
			ArrayList<Integer> result = instanceAlgorithm.kasai(text);
//			
//			int len1 = textCode1.length();
//			for(int i = 0; i < len1; i++) {
//				System.out.println(result.get(i) + " " + result.get(i+len1));
//			}
			/*
			System.out.println("XXX");
			long m = 0, t = 0;
			
			float tempMaching = (float) (Math.max(textCode1.length(), textCode2.length()) / 2.0 - 1);
			int valMax = -1;
			for(int xx : result) {
				valMax = Math.max(valMax, xx);
				if(xx >= tempMaching) {
					m += (xx - 1);
					t += (xx/2) - 1;
				}
			}
			
			System.out.println(textCode1.length()+ " - " + textCode2.length());
			System.out.println(m + " - " + t + " : " + valMax);
			
			System.out.println(result);
			System.out.println("XXX"); */
			 
			int diffMax = result.get(0);
			for (int number : result) {
				diffMax = Math.max(diffMax, number);
			}

			int sizeOfTextCode1 = textCode1.length();
			int sizeOfTextCode2 = textCode2.length();

			//System.out.println("DiffMax = " + diffMax);
//			System.out.println("numberEditdistance = " + numberEditdistance);
			return Math.max((Math.max(sizeOfTextCode1 - diffMax, 1) / (double) sizeOfTextCode2) * 100, 0.0);
		}

		public Double call() {
			synchronized (this) {
				double ratio = 0;
			//	System.out.println(algorithmType);
				switch (algorithmType) {
				case EDIT_DISTANCE:
					ratio = computeUseEditDistance(listCode1, listCode2);
					break;
				case BRUTE_FORCES:
					ratio = computeBruteForces(listCode1, listCode2);
					break;
//				case Z_FUNCTION:
//					ratio = computeZfunction(listCode1, listCode2);
//					break;
//				case Z_FUNCTION:
//					ratio = computeSuffixArray(listCode1, listCode2);
//					break;
				}

				return ratio;
			}
		}
	}

	static class Classify implements Runnable {
		public static final int TWO_FILE = 1;
		public static final int LIST_FILE = 2;
		public static final int THREAD_MAX = 10;
		public static double RATIO_EXPECTED = 70.0;
//		public static final String PATH = "files\\list code";
		List<CodeInformation> listAllCode;
		int type, algorithmType = 1;
		static boolean checkStartNotDone;
		
		static long timeStart, timeEnd;

		public Classify(int type, int algorithmType, List<CodeInformation> listAllCode, double ratio) {
			this.type = type;
			this.algorithmType = algorithmType;
			this.listAllCode = listAllCode;
			// System.out.println(this.listAllCode);
			Classify.RATIO_EXPECTED = ratio;
			checkStartNotDone = true;
		}

		private void preStart(List<CodeInformation> listAllCode) {
			timeStart = System.currentTimeMillis();
			ExecutorService executorService = Executors.newSingleThreadExecutor();

			for (int i = 0; i < listAllCode.size(); i++) {
				synchronized (listAllCode) {
					CodeInformation codeCurrent = listAllCode.get(i);
					Future<CodeInformation> result = executorService.submit(new ManagerCodeSystem(codeCurrent));
					try {
						listAllCode.set(i, result.get());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		private void start(int type, List<CodeInformation> listAllCode) {
//			File folder = new File(PATH);
//			List<CodeInformation> listAllCode = new FileService().readCodeInfolder(folder);

			if (listAllCode != null) {
				checkStartNotDone = true;
				preStart(listAllCode);
				chooseType(listAllCode, type);
			}
		}

		private void chooseType(List<CodeInformation> listAllCode, int type) {
			switch (type) {
			case TWO_FILE:
				runningCompare(listAllCode);
				break;

			case LIST_FILE:
				CompareList(listAllCode);
				break;
			}
		}

		private void runningCompare(List<CodeInformation> listAllCode) {
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			Future<Double> ratioResult = executorService.submit(new SystemRunningCompute(
					listAllCode.get(0).getListCode(), listAllCode.get(1).getListCode(), algorithmType));

			try {
				double ratio = ratioResult.get();
				MySystemManager.ratio = ratio;
		//		System.out.println("ratio = " + ratio);

				checkStartNotDone = false;
				if (!executorService.isShutdown())
					executorService.shutdownNow();

			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			}
		}

		private void CompareList(List<CodeInformation> listAllCode) {
			running(listAllCode);
		}

		private void running(List<CodeInformation> listAllCode) {
			ExecutorService executorService = Executors.newFixedThreadPool(THREAD_MAX);
			int number = 0;
			for (int i = 0; i < listAllCode.size() - 1; i++) {
				synchronized (sameList) {
					ArrayList<String> listSameTmp = new ArrayList<>();
					CodeInformation info1 = listAllCode.get(i);
//				System.out.println(Utility.convertListToString(info1.getListCode()));
					boolean checkRatio = false;

					listSameTmp.add(info1.getFileName());

					for (int j = i + 1; j < listAllCode.size(); j++) {
						CodeInformation info2 = listAllCode.get(j);
//						System.out.println(Utility.convertListToString(info2.getListCode()));

						Future<Double> ratioResult = executorService.submit(
								new SystemRunningCompute(info1.getListCode(), info2.getListCode(), algorithmType));

						try {
							double ratio = ratioResult.get();

							if (ratio >= RATIO_EXPECTED) {
								checkRatio = true;
								listSameTmp.add(info2.getFileName());
//								System.out.println(ratio);
//								System.out.println(Utility.convertListToString(info1.getListCode()));
//								System.out.println(Utility.convertListToString(info2.getListCode()));
//								System.out.println("================================");
							}
						} catch (InterruptedException | ExecutionException ex) {
							ex.printStackTrace();
						}

					}
					if (checkRatio) {
						timeEnd = System.currentTimeMillis();
						System.out.println("TIME TOTAL = " + (timeEnd - timeStart));
						sameList.put(number, listSameTmp);
						number++;
					}
				}
//				System.out.println();
			}

//			System.out.println(sameList);
			checkStartNotDone = false;
			if (!executorService.isShutdown())
				executorService.shutdown();
		}

		private List<CodeInformation> getlistAllCode_Classify() {
			while (checkStartNotDone) {
			}

			return listAllCode;
		}

		@Override
		public void run() {
			if (type == 0)
				type = 1;
			start(type, listAllCode);
		}
	}

	public static List<CodeInformation> getlistAllCode() {
		return c.getlistAllCode_Classify();
	}

	public static Map<Integer, List<String>> getSameList() {
		while (threadClassify.isAlive()) {
		}
		return MySystemManager.sameList;
	}
	
	public static void clear() {
		MySystemManager.sameList.clear();
	}

	public static double getRatio() {
		while (threadClassify.isAlive()) {
		}
		return ratio;
	}

	public static void start(int type, int algorithmType, List<CodeInformation> listAllCode, double ratio) {
		c = new Classify(type, algorithmType, listAllCode, ratio);
		threadClassify = new Thread(c);
		threadClassify.start();
	}
}
