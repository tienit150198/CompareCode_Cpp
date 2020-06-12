import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManagerAlgorithm {
	private static ManagerAlgorithm instance;

	public static ManagerAlgorithm getInstance() {
		if (instance == null) {
			instance = new ManagerAlgorithm();
		}

		return instance;	
	}

	public synchronized Integer bruteForceCompare(String txt1, String txt2) {
		List<Integer> listResult = new ArrayList<Integer>();

		for (int i = 0; i < txt1.length(); i++) {
			for (int j = 0; j < txt2.length(); j++) {
				if (txt1.charAt(i) == txt2.charAt(j)) {
					int c = 0;
					int x = i, y = j;
					while (x < txt1.length() && y < txt2.length()) {
						if (txt1.charAt(x) == txt2.charAt(y)) {
							c++;
							x++;
							y++;
						} else {
							x++;
							y++;
						}
					}
					listResult.add(c);
				}
			}
		}

		return listResult.stream().max(Integer::compare).get();

	}

	public synchronized int editDistDP(String str1, String str2, int m, int n) {
		// Create a table to store results of subproblems
		int dp[][] = new int[m + 1][n + 1];

		// Fill d[][] in bottom up manner
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i == 0)
					dp[i][j] = j;

				else if (j == 0)
					dp[i][j] = i;

				else if (str1.charAt(i - 1) == str2.charAt(j - 1))
					dp[i][j] = dp[i - 1][j - 1];

				else
					dp[i][j] = 1 + Utility.min(dp[i][j - 1], // Insert
							dp[i - 1][j], // Remove
							dp[i - 1][j - 1]); // Replace
			}
		}

		return dp[m][n];
	}

	/******************** Z function ******************/
	public synchronized int ZSearch(String text, String pattern) {

		// Create concatenated string "P$T"
		String concat = pattern + "$" + text;

		int l = concat.length();

		int Z[] = new int[l];

		getZarr(concat, Z);
		int countFound = 0;
		for (int i = 0; i < l; ++i) {

			if (Z[i] == pattern.length()) {
				// System.out.println("Pattern found at index " + (i - pattern.length() - 1));
				countFound++;
			}
		}
		return countFound;
	}

	private synchronized void getZarr(String str, int[] Z) {

		int n = str.length();

		int L = 0, R = 0;

		for (int i = 1; i < n; ++i) {

			if (i > R) {

				L = R = i;

				while (R < n && str.charAt(R - L) == str.charAt(R))
					R++;

				Z[i] = R - L;
				R--;

			} else {

				int k = i - L;

				if (Z[k] < R - i + 1)
					Z[i] = Z[k];

				else {

					L = i;
					while (R < n && str.charAt(R - L) == str.charAt(R))
						R++;

					Z[i] = R - L;
					R--;
				}
			}
		}
	}

	/*************** end Z function *****************/

	/*************** KMP *****************/
	public synchronized int KMPSearch(String pat, String txt) {
		int M = pat.length();
		int N = txt.length();

		int lps[] = new int[M];
		int j = 0; // index for pat[]

		computeLPSArray(pat, M, lps);

		int i = 0; // index for txt[]
		int countFound = 0;
		while (i < N) {
			if (pat.charAt(j) == txt.charAt(i)) {
				j++;
				i++;
			}
			if (j == M) {
				// System.out.println("Found pattern " + "at index " + (i - j));
				countFound++;
				j = lps[j - 1];
			}

			else if (i < N && pat.charAt(j) != txt.charAt(i)) {
				if (j != 0)
					j = lps[j - 1];
				else
					i = i + 1;
			}
		}
		return countFound;
	}

	private synchronized void computeLPSArray(String pat, int M, int lps[]) {
		// length of the previous longest prefix suffix
		int len = 0;
		int i = 1;
		lps[0] = 0; // lps[0] is always 0

		// the loop calculates lps[i] for i = 1 to M-1
		while (i < M) {
			if (pat.charAt(i) == pat.charAt(len)) {
				len++;
				lps[i] = len;
				i++;
			} else // (pat[i] != pat[len])
			{
				if (len != 0) {
					len = lps[len - 1];

				} else // if (len == 0)
				{
					lps[i] = len;
					i++;
				}
			}
		}
	}

	/*************** end KMP *****************/

	/*************** Start Suffix Array *****************/
	public synchronized ArrayList<Integer> buildSuffixArray(String s) {
		int n = s.length();
		Suffix[] su = new Suffix[n];

		for (int i = 0; i < n; i++) {
			int index = i;
			int rank = s.charAt(i) - 'a';
			int next = -1;
			if ((i + 1 < n)) {
				next = s.charAt(i + 1) - 'a';
			}

			su[i] = new Suffix(index, rank, next);
		}

//		for (int i = 0; i < n; i++) {
//			su[i].next = (i + 1 < n ? su[i + 1].rank : -1);
//		}

		Arrays.sort(su);
		int[] ind = new int[n];

		for (int length = 4; length < 2 * n; length <<= 1) {
			int rank = 0, prev = su[0].rank;
			su[0].rank = rank;
			ind[su[0].index] = 0;
			for (int i = 1; i < n; i++) {
				if (su[i].rank == prev && su[i].next == su[i - 1].next) {
					prev = su[i].rank;
					su[i].rank = rank;
				} else {
					prev = su[i].rank;
					su[i].rank = ++rank;
				}
				ind[su[i].index] = i;
			}

			for (int i = 0; i < n; i++) {
				int nextP = su[i].index + length / 2;
				su[i].next = (nextP < n) ? su[ind[nextP]].rank : -1;
			}
			Arrays.sort(su);
		}
		ArrayList<Integer> suf = new ArrayList<>();
//		int[] suf = new int[n];

		for (int i = 0; i < n; i++) {
			suf.add(su[i].index);
//			suf[i] = su[i].index;
		}

		// Return the suffix array
		return suf;
	}

	public synchronized ArrayList<Integer> kasai(String txt) {
		System.out.println(txt);
		ArrayList<Integer> suffixArr = buildSuffixArray(txt);
		/*
		 * System.out.println("YYY"); System.out.println(suffixArr);
		 * System.out.println("YYY");
		 */
		int _size = suffixArr.size();

		ArrayList<Integer> lcp = new ArrayList<>(_size);
		for (int i = 0; i < _size; i++) {
			lcp.add(0);
		}

		int[] invSuff = new int[_size];

		for (int i = 0; i < _size; i++) {
			invSuff[suffixArr.get(i)] = i;
		}

		int k = 0;

		for (int i = 0; i < _size; i++) {
			if (invSuff[i] == _size - 1) {
				k = 0;
				continue;
			}

			int j = suffixArr.get(invSuff[i] + 1);
			while (i + k < _size && j + k < _size && txt.charAt(i + k) == txt.charAt(j + k))
				k++;

			lcp.set(invSuff[i], k);
			if (k > 0) {
				k--;
			}
		}

		return lcp;
	}

	/*************** End Suffix Array *****************/

	public synchronized List<Long> convertHashToNumber(String text, int gram, int base) {
		List<Long> resultHash = new ArrayList<>();

		List<String> listGrams = ngrams(gram, text);

		// lấy hash đầu tiên, vì hash đầu tiên sẽ lấy hết, những hash sau dựa trên cái
		// đầu tiên để tính tiếp
		long firstHashNumber = hashNumber(listGrams.get(0), base);
		long nextHashNumber = firstHashNumber;

		resultHash.add(firstHashNumber);
		for (int i = 1; i < listGrams.size(); i++) {
			String textBefore = listGrams.get(i - 1);
			String textCurrent = listGrams.get(i);

			int numBefore = textBefore.charAt(0) - 'a';
			int numCurrent = textCurrent.charAt(gram - 1) - 'a';
			long numSub = (long) (numBefore * Math.pow(base, gram - 1));

			// H(“orwhi”) = (H(“forwh”) – 5 × (26^4) ) × 26 + 8 = 6711518
			// thực hiện trừ
			long hashCurrent = (nextHashNumber - numSub) * base + numCurrent;
			nextHashNumber = hashCurrent;

			resultHash.add(hashCurrent);
		}

		return resultHash;
	}

	private long hashNumber(String text, int base) {
		long res = 0;
		int _sizeMax = text.length() - 1;
		for (int i = 0; i < text.length(); i++) {
			int num = text.charAt(i) - 'a';

			res = (long) (res + num * Math.pow(base, _sizeMax - i));
		}

		return res;
	}

	public synchronized List<String> ngrams(int n, String str) {
		List<String> ngrams = new ArrayList<String>();
		for (int i = 0; i < str.length() - n + 1; i++) {
			ngrams.add(str.substring(i, i + n));
		}
		return ngrams;
	}
}
