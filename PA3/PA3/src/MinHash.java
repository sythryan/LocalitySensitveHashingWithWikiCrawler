//Author: Syth Ryan

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class MinHash {
	
	private static int numMinHashPermutations;
	private static Map<String, Map<String, Integer>> termDocumentMatrix;
	static int[][] minHashMatrix;
	private static ArrayList<String> setOfAllTerms = new ArrayList<String>();
	private static File currentDirectory;
	private static File[] listOfFiles; //order of files is not guaranteed so we must use a stored list (shadow) to keep an exact reference.
	
	//Required for Random Hashing because it would be hard to re-do the hashes exactly. stores random numbers <a, b>
	private static List<List<Integer>> hashFunctionValues = new ArrayList<List<Integer>>();
	private static int p; // prime number
	
	/* folder is the name of a folder containing our document collection for which we wish to construct MinHash matrix. 
	 * numPermutations denotes the number of permutations to be used in creating the MinHash matrix.*/
	MinHash(String folder, int numPermutations) {
		numMinHashPermutations = numPermutations;
		currentDirectory = new File(folder);
		listOfFiles = currentDirectory.listFiles();
			
		initializeTermDocumentMatrix(listOfFiles);
		// fill out the termDocumentMatrix
		// find all the terms in every document, document maps not containing the key of a term is assumed to have frequency of 0 (saving memory)
		findAllTerms(listOfFiles);
		p = nextPrime(setOfAllTerms.size()); 
		minHashMatrix = minHashMatrix();		
	}
	
	/* returns the column of minHash values from all permutations based on the given file */
	private int[][] minHashDocuments(File[] fileList, int numPermutations) {
		int[][] matrix = new int[fileList.length][numPermutations]; //K x M
		
		for (int i = 0; i < fileList.length; i++) { // for each document		
			// min hashing for each document
			int[] tempListOfMinHashedValues = new int[numPermutations];
			for (int n = 0; n < numPermutations; n++) { // for  each Permutation
				// find the min value of all terms
				tempListOfMinHashedValues[n] = minHashedTermValue(p, hashFunctionValues.get(n).get(0), hashFunctionValues.get(n).get(1), termDocumentMatrix.get(listOfFiles[i].toString()));
			}
			// fill document i's values in the matrix
			matrix[i] = tempListOfMinHashedValues;
		}
		return matrix;
	}
	
	/* Returns the lowest randomly hashed value of setOfString based on prime p and random values a and b */
	private int minHashedTermValue(int p, long a, long b, Map<String, Integer> setOfStrings) {
		int min = Integer.MAX_VALUE;
			for (int i = 0; i < setOfAllTerms.size(); i++) { // for all terms
				if (setOfStrings.containsKey(setOfAllTerms.get(i))) {
					min = Integer.min(min, (int)RandomHashCode(p, a, b, setOfAllTerms.get(i)));
				}
		}
		return min;
	}
	
	/* Returns a hash code based on prime p, hash code of String s and random values a and b [(a * s + b) % p] */
	private static long RandomHashCode(int p, long a, long b, String s) { // note using longs as parameters for calculations purposes only. prime p will make this 
		return (a * Math.abs(s.hashCode()) + b) % (long)p;					      // a for sure int after since it is sent in as an int. this means we wont have to worry about trimming values
	}
	
	/* Initializes the term document matrix by placing term "" with frequency 0 as the first element */
	private void initializeTermDocumentMatrix(File[] fileList) {
		termDocumentMatrix = new HashMap<String, Map<String, Integer>>();
		for (int i = 0; i < fileList.length; i++) { // for each document
			Map<String, Integer> tempMap = new HashMap<String,Integer>();
			termDocumentMatrix.put(fileList[i].toString(), tempMap);
		}
	}
	
	/* Creates a map of unique terms for each document found in the directory, terms not found in a documents map should be assumed as 0 frequency */
	private void findAllTerms(File[] fileList) {
		// go through each document		
		for (int i = 0; i < fileList.length; i++) {
			Scanner myFileScanner = null;
		    try {
		    	myFileScanner = new Scanner(fileList[i]);
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();  
		    }
		    while (myFileScanner.hasNextLine()) {
	            Scanner myLineScanner = new Scanner(myFileScanner.nextLine());
		        while (myLineScanner.hasNext()) {
		            String s = myLineScanner.next().toLowerCase(); //grab next word and make case insensitive
		            s.replaceAll(".", "");
		            s.replaceAll(",", "");
		            s.replaceAll(":", "");
		            s.replaceAll(";", "");
		            s.replaceAll("'", "");
		            
		            if ((s.length() >= 3) && !(s.equals("the"))) {
		               	// add Word to the term document matrix. if it already exists, increment otherwise initialize term frequency to 1
			            if (termDocumentMatrix.get(fileList[i].toString()).containsKey(s)) {
			            	termDocumentMatrix.get(fileList[i].toString()).put(s, termDocumentMatrix.get(fileList[i].toString()).get(s) + 1);
			            } else {
			            	termDocumentMatrix.get(fileList[i].toString()).put(s, 1);
			            }
			            if (!setOfAllTerms.contains(s)) {
			            	setOfAllTerms.add(s);
			            }
		            }
		        }
		        myLineScanner.close();
		    }
		    myFileScanner.close();
		}
	}
		
	/* Returns an array of String consisting of all the names of files in the document collection. */
	public static String[] allDocs() {
		return currentDirectory.list();
	}

	/* Get names of two files (in the document collection) file1 and file2 as parameters and returns the exact Jaccard Similarity of the files. */	
	public double exactJaccard(String file1, String file2) {
		// create binary vectors for each document based on terms
		BitSet[] documentBinaryVectors = createBinaryVectors(file1, file2);	
		
		// note since we are only working with 0 and 1, squaring would result in the same number. also square root then squaring cancel out in my overall calculation.
		double dotProduct = (double) vectorDotProduct(documentBinaryVectors[0], documentBinaryVectors[1]);
		return dotProduct  / (singleVectorsum(documentBinaryVectors[0]) + singleVectorsum(documentBinaryVectors[1]) - dotProduct);
	}
	
	private BitSet[] createBinaryVectors(String file1, String file2) {
		BitSet[] vectors = new BitSet[2];

		// file 1
		vectors[0] = fileVector(file1);
		// file 2
		vectors[1] = fileVector(file2);

		return vectors;
	}
	
	private BitSet fileVector(String fileName) {
		int fileIndex = 0;
		BitSet retVal = new BitSet();
		
		while ((fileIndex < listOfFiles.length) && !(listOfFiles[fileIndex].toString().contains(fileName))) {
			fileIndex++;
		}
		
		for (int i = 0; i < setOfAllTerms.size(); i++) { //for each term 
			if (termDocumentMatrix.get(listOfFiles[fileIndex].toString()).containsKey(setOfAllTerms.get(i))) {
				retVal.set(i, true); // set the ith bit to 1 for the ith term
			} // else skip to next term and leave the nth bit as 0
		}
		return retVal;
	}
	
	private static double singleVectorsum(BitSet vector) {
		int sum = 0;
		for (int i = 0; i < vector.size(); i++) {
			if (vector.get(i)) {
				sum++;
			}
		}
		
		return sum;
	}
	
	private static double vectorDotProduct(BitSet vectorA, BitSet vectorB) {
		int sum = 0;
		for (int i = 0; i < vectorA.size(); i++) {
			if (vectorA.get(i) && vectorB.get(i)) {
				sum ++;
			}
		}
		return sum;
	}

	/* Returns the MinHash signature of the document named fileName, which is an array of ints. */
	public static int[] minHashSig(String fileName) {		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].toString().equals(fileName)) {
				return minHashMatrix[i];
			}
		}
		return null;
	}
	
	/* Estimates and returns the Jaccard similarity of documents file1 and file2 by comparing the MinHash signatures of file1 and file2. */
	public double approximateJaccard(String file1, String file2) {
		int indexA = 0;
		int indexB = 0;
		
		while ((indexA < listOfFiles.length) && !(listOfFiles[indexA].toString().contains(file1))) {
			indexA++;
		}
		
		while ((indexB < listOfFiles.length) && !(listOfFiles[indexB].toString().contains(file2))) {
			indexB++;
		}
		
		int numMatches = 0;
		// compute number of matches in the hash signatures and divide that by the number of permutations.
		for (int i = 0; i < numMinHashPermutations; i++) {
			if (minHashMatrix[indexA][i] == minHashMatrix[indexB][i]){
				numMatches++;
			}
		}
		return (double) numMatches / (double) numMinHashPermutations;
	}
	
	/* Returns the MinHash Matrix of the collection. */
	public int[][] minHashMatrix() {
		java.util.Random randomNumbers = new java.util.Random();
		
		// Generate minHash permutations
		// Generate hash function <a, b> values
		for(int i = 0; i < numMinHashPermutations; i++) {
			int a = Math.abs(randomNumbers.nextInt());
			int b = Math.abs(randomNumbers.nextInt());
			List<Integer> tempList = new ArrayList<Integer>();
			tempList.add(a);
			tempList.add(b);
			hashFunctionValues.add(tempList);
		}
		
		minHashMatrix = new int[listOfFiles.length][numMinHashPermutations];
		// fill out the minHashMatrix
		minHashMatrix = minHashDocuments(listOfFiles, numMinHashPermutations);
		return minHashMatrix;
	}
	
	/* Returns the number of terms in the document collection. */
	public static int numTerms(){
		return listOfFiles.length;
	}
	
	/* Returns the number of permutations used to construct the MinHash matrix. */
	public static int numPermutations() {
		return numMinHashPermutations;
	}
	
	/* Returns the next prime larger than the given input n */
	private static int nextPrime(int n) {
	    boolean primeFlag = false;
	    while (!primeFlag) {
	        n += 1;
	        int m = (int) Math.ceil(Math.sqrt(n));

	        primeFlag = true;
	        for (long i = 2; i <= m; i++) {
	            if (n % i == 0) {
	            	primeFlag = false;
	                break;
	            } 
	        }
	    }
	    return n;
	}
	
}
