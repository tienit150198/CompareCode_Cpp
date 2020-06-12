package filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class demo {
	public static class Suffix implements Comparable<Suffix> {
		int index;
		int rank;
		int next;

		public Suffix(int ind, int r, int nr) {
			index = ind;
			rank = r;
			next = nr;
		}

		@Override
		public int compareTo(Suffix s) {
			if (rank != s.rank)
				return Integer.compare(rank, s.rank);
			return Integer.compare(next, s.next);
		}
	}

	public static ArrayList<Integer> buildSuffixArray(String s) {
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
				su[i].next = nextP < n ? su[ind[nextP]].rank : -1;
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

	static ArrayList<Integer> kasai(String txt) {
		ArrayList<Integer> suffixArr = buildSuffixArray(txt);
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

	static void printArr(ArrayList<Integer> arr, int n) {
		for (Integer integer : arr) {
			System.out.print(integer + " ");
		}
		System.out.println();
	}

	static ArrayList<Integer> hashString(String txt) {
		ArrayList<Integer> res = new ArrayList<>();

		List<String> listStr = ngrams(5, txt);

		for (String text : listStr) {
			long numberHash = hashNumber(text, 26);
		}

		return res;
	}

	private static long hashNumber(String text, int base) {
		long res = 0;
		int _sizeMax = text.length() - 1;
		for (int i = 0; i < text.length(); i++) {
			int num = text.charAt(i) - 'a';

			res = (long) (res + num * Math.pow(base, _sizeMax - i));
		}

		return res;
	}

	private static long getNumber(int number, int base, int position) {
		return (long) (number * Math.pow(base, position));
	}

	private static int hashNumberInCharacter(int numHashBefore, int base, char characterBefore, char characterNext,
			int position) {
		int numNext = characterNext - 'a';
		return numHashBefore + numNext;
	}

	public static List<String> ngrams(int n, String str) {
		List<String> ngrams = new ArrayList<String>();
		for (int i = 0; i < str.length() - n + 1; i++) {
			ngrams.add(str.substring(i, i + n));
		}
		return ngrams;
	}

	public synchronized static List<Integer> convertHashToNumber(String text, int gram, int base) {
		List<Integer> resultHash = new ArrayList<>();

		List<String> listGrams = ngrams(gram, text);

		// lấy hash đầu tiên, vì hash đầu tiên sẽ lấy hết, những hash sau dựa trên cái
		// đầu tiên để tính tiếp
		int firstHashNumber = (int) hashNumber(listGrams.get(0), base);
		int nextHashNumber = firstHashNumber;

		resultHash.add(firstHashNumber);
		for (int i = 1; i < listGrams.size(); i++) {
			String textBefore = listGrams.get(i - 1);
			String textCurrent = listGrams.get(i);

			int numBefore = textBefore.charAt(0) - 'a';
			int numCurrent = textCurrent.charAt(gram - 1) - 'a';
			int numSub = (int) (numBefore * Math.pow(base, gram - 1));

			// H(“orwhi”) = (H(“forwh”) – 5 × (26^4) ) × 26 + 8 = 6711518
			// thực hiện trừ
			int hashCurrent = (nextHashNumber - numSub) * base + numCurrent;
			nextHashNumber = hashCurrent;

			resultHash.add(hashCurrent);
		}

		return resultHash;
	}

	public static void main(String[] args) {
//		String str = "forwhiledododofordo";
		int gram = 5;
		int base = 26;
//		List<String> listGrams = ngrams(gram, str);
		String txt = "GATAGACA$AGA#";
		String str1 = "abxbbbbbb";
		String str2 = "abb";
//		String str = str1 + "$" + str2 + "#";
//		ArrayList<Integer> hash1 = (ArrayList<Integer>) convertHashToNumber(str1, gram, base);
//		ArrayList<Integer> hash2 = (ArrayList<Integer>) convertHashToNumber(str2, gram, base);
//		ArrayList<Integer> hash = (ArrayList<Integer>) convertHashToNumber(str, gram, base);
//
////		ArrayList<Integer> arrTxt = hashString(txt);
////		int n = txt.length();
//		ArrayList<Integer> suffArr1 = buildSuffixArray(txt);
//		ArrayList<Integer> suffArr2 = buildSuffixArray(str2);
//		ArrayList<Integer> suffArr = buildSuffixArray(txt);
////		System.out.println("Suffix Array: ");
//		System.out.println("text = " + txt);
//		System.out.println("SA: ");
//		printArr(suffArr, suffArr.size());
//
//		ArrayList<Integer> lcp = kasai(txt);
//		System.out.println("Lcp: ");
//		System.out.println(lcp);

		//compare(str1, str2);
	}

	public List<Integer> compare(String txt1, String txt2) {
		List<Integer> listResult = new ArrayList<Integer>();

		for (int i = 0; i < txt1.length(); i++) {
			for (int j = 0; j < txt2.length(); j++) {
				if (txt1.charAt(i) == txt2.charAt(j)) {
					int c = 0;
					int x = i, y = j;
					while (x < txt1.length() && y < txt2.length()) {
						if(txt1.charAt(x) == txt2.charAt(y)) {
							c++;
							x++;
							y++;
						}else {
							x++;
							y++;
						}
					}
					listResult.add(c);
				}
			}
		}
		
		return listResult;

	}

}
