package il.ac.tau.cs.sw1.ex5;


import java.io.*;
import java.util.Arrays;

public class BigramModel {
	public static final int MAX_VOCABULARY_SIZE = 14500;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;

	String[] mVocabulary;
	int[][] mBigramCounts;

	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException {
		mVocabulary = buildVocabularyIndex(fileName);
		mBigramCounts = buildCountsArray(fileName, mVocabulary);

	}


	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public String[] buildVocabularyIndex(String fileName) throws IOException { // Q 1
		String[] vocabulary = new String[MAX_VOCABULARY_SIZE];
		FileReader file = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(file);
		int wordCount = 0;
		String line;
		while ((line = reader.readLine()) != null && wordCount < MAX_VOCABULARY_SIZE) {
			String[] words = line.split("\\s+");
			for (String word : words) {
				String fixedWord = fixedWordFunc(word);
				if (isLegalWord(fixedWord) && !containsWord(vocabulary, fixedWord)) {
					vocabulary[wordCount] = fixedWord;
					wordCount++;
				}
			}
		}
		reader.close();
		return Arrays.copyOf(vocabulary, wordCount);
	}

	private String fixedWordFunc(String word) {
		word = word.toLowerCase();
		if (word.matches("\\d+")) {
			word = SOME_NUM;
		}
		return word;
	}

	private boolean isLegalWord(String word) {
		return word.matches(".*[a-zA-Z].*") || word.matches("\\d+");
	}

	private boolean containsWord(String[] vocabulary, String word) {
		for (String curWord : vocabulary) {
			if (curWord != null && curWord.equals(word)) {
				return true;
			}
		}
		return false;
	}
//	public static void main(String[] args) {
//		BigramModel model = new BigramModel();
//		String fileName = "C:\\Users\\תומר\\.gnupg\\hw_5-tomerud\\resources\\hw5\\all_you_need.txt";
//		try {
//			model.initModel(fileName);
//			String[] arr = model.buildVocabularyIndex(fileName);
//			System.out.println(Arrays.toString(arr));
//			int n = arr.length;
//			System.out.println(n);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}


	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException { // Q - 2
		int n = vocabulary.length;
		int[][] matrix = new int[n][n];
		FileReader file = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(file);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] words = line.split("\\s+");
			int m = words.length;
			for (int i = 0; i < m - 1; i++) {
				String word1 = fixedWordFunc(words[i]);
				String word2 = fixedWordFunc(words[i + 1]);
				int idx1 = getIndex(vocabulary, word1);
				int idx2 = getIndex(vocabulary, word2);
				if (idx1 != -1 && idx2 != -1) {
					matrix[idx1][idx2]++;
				}
			}
		}
		reader.close();
		return matrix;
	}

	private int getIndex(String[] vocabulary, String word) {
		int n = vocabulary.length;
		for (int i = 0; i < n; i++) {
			if (vocabulary[i].equals(word)) {
				return i;
			}
		}
		return -1;
	}


	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException { // Q-3
		FileWriter file = new FileWriter(fileName + VOC_FILE_SUFFIX);
		BufferedWriter writer = new BufferedWriter(file);
		int n = mVocabulary.length;
		writer.write(n + " words" + System.lineSeparator());
		for (int i = 0; i < n; i++) {
			writer.write(i + "," + mVocabulary[i] + System.lineSeparator());
		}
		writer.close();
		FileWriter file2 = new FileWriter(fileName + COUNTS_FILE_SUFFIX);
		BufferedWriter writer2 = new BufferedWriter(file2);
		int m = mBigramCounts.length;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				if (mBigramCounts[i][j] != 0) {
					file2.write(i + "," + j + ":" + mBigramCounts[i][j] + System.lineSeparator());
				}
			}
		}
		writer2.close();
	}


	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException { // Q - 4
		String line1;
		String line2;
		FileReader reader1 = new FileReader(fileName + VOC_FILE_SUFFIX);
		BufferedReader readerVoc = new BufferedReader(reader1);
		line1 = readerVoc.readLine();
		String[] firstLine = line1.split(" ");
		String[] forVocabulary = new String[Integer.parseInt(firstLine[0])];
		while ((line1 = readerVoc.readLine()) != null) {
			String[] words = line1.split(",");
			forVocabulary[Integer.parseInt(words[0])] = words[1];
		}
		mVocabulary = forVocabulary;
		readerVoc.close();
		int n = mVocabulary.length;
		int[][] forBigram = new int[n][n];
		FileReader reader2 = new FileReader(fileName + COUNTS_FILE_SUFFIX);
		BufferedReader readerCounts = new BufferedReader(reader2);
		while ((line2 = readerCounts.readLine()) != null) {
			String[] words = line2.split(":");
			int index1 = words[0].charAt(0);
			int index2 = words[0].charAt(2);
			forBigram[index1 - '0'][index2 - '0'] = Integer.parseInt(words[1]);
		}
		mBigramCounts = forBigram;
		readerCounts.close();
	}


	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word) {  // Q - 5
		int n = mVocabulary.length;
		if (n == 0) {
			return ELEMENT_NOT_FOUND;
		}
		for (int i = 0; i < n; i++) {
			if (word.equals(mVocabulary[i])) {
				return i;
			}
		}
		return ELEMENT_NOT_FOUND;
	}


	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2) { //  Q - 6
		int index1 = getWordIndex(word1);
		int index2 = getWordIndex(word2);
		if (index1 == ELEMENT_NOT_FOUND || index2 == ELEMENT_NOT_FOUND) {
			return 0;
		}
		return mBigramCounts[index1][index2];
	}


	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word) { //  Q - 7
		int indexOfWord = getWordIndex(word);
		int n = mBigramCounts.length;
		int curMax = 0;
		int maxIndex = -1;
		for (int i = 0; i < n; i++) {
			int wordCounts = mBigramCounts[indexOfWord][i];
			if (wordCounts > curMax) {
				curMax = wordCounts;
				maxIndex = i;
			}
		}
		if (curMax > 0) {
			return mVocabulary[maxIndex];
		}
		return null;
	}


	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence) {  //  Q - 8
		String[] sentenceArray = sentence.split(" ");
		int n = sentenceArray.length;
		if (n == 0) {
			return true;
		}
		if (n == 1) {
			if (containsWord(mVocabulary, sentenceArray[0])) {
				return true;
			}
			return false;
		}
		int countLegalCouple = 0;
		for (int i = 0; i < n - 1; i++) {
			int numOfOccurences = getBigramCount(sentenceArray[i], sentenceArray[i + 1]);
			if (numOfOccurences > 0) {
				countLegalCouple++;
			}
		}
		if (countLegalCouple == n - 1) {
			return true;
		}
		return false;
	}


	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calcluates CosineSim
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2) { //  Q - 9
		int n = arr1.length;
		int numOfZerosaArray1 = 0;
		int numOfZerosArray2 = 0;
		for (int i = 0; i < n; i++) {
			if (arr1[i] == 0) {
				numOfZerosaArray1++;
			}
			if (arr2[i] == 0) {
				numOfZerosArray2++;
			}
		}
		if (numOfZerosaArray1 == n || numOfZerosArray2 == n) {
			return -1;
		}
		double sumMone = 0;
		for (int i = 0; i < n; i++) {
			sumMone += arr1[i] * arr2[i];
		}
		double mehane1 = vectorFunc(arr1);
		double mehane2 = vectorFunc(arr2);
		double result = sumMone / (mehane1 * mehane2);
		return result;
	}

	public static double vectorFunc(int[] arr) {
		int n = arr.length;
		double sum = 0;
		for (int i = 0; i < n; i++) {
			sum += arr[i] * arr[i];
		}
		double sumRoot = Math.sqrt(sum);
		return sumRoot;
	}


	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized),
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word) { //  Q - 10
		int n = mVocabulary.length;
		int[] wordArray = new int[n];
		int index = getWordIndex(word);
		wordArray = mBigramCounts[index];
		int[] curArray = new int[n];
		double currentRating = 0;
		double curBestRating = 0;
		String bestWord = mVocabulary[0];
		for (int i = 0; i < n; i++) {
			if (i == index) {
				i++;
			}
			curArray = mBigramCounts[i];
			currentRating = calcCosineSim(wordArray, curArray);
			if (currentRating > curBestRating) {
				curBestRating = currentRating;
				bestWord = mVocabulary[i];
			}
		}
		return bestWord;
	}
}

	

