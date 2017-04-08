//Author Syth Ryan

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

public class debug {

	public static void main(String[] args) {
		String[] topics = {"tennis"};
		WikiCrawler myCrawlwer = new WikiCrawler("/wiki/Seed5.html", topics, 10, "testOutput.txt", true);
		myCrawlwer.crawl();
		/*
		PageRank myRanker = new PageRank("Principle_of_maximum_entropy.txt", 0.01);
		
		System.out.println("Top 10 page rank vertecies" + myRanker.topKPageRank(10));
		System.out.println("Top 10 page rank vertecies" + myRanker.topKInDegree(10));
		
		String[] setA = myRanker.topKInDegree(10);
		String[] setB = myRanker.topKOutDegree(10);
		String[] setC = myRanker.topKPageRank(10);
		
		String [][] allSets = {setA, setB, setC};
		String[] allLinks = new String[allSets[0].length + allSets[1].length + allSets[2].length];
		//record all links
		
		
		allLinks = concatAll(setA, setB, setC);
		ArrayList<String> allLinksArrayList = new ArrayList<String>(Arrays.asList(allLinks));
		
		BitSet vectorA = createVector(new ArrayList<String>(Arrays.asList(setA)), allLinksArrayList);
		BitSet vectorB = createVector(new ArrayList<String>(Arrays.asList(setB)), allLinksArrayList);
		BitSet vectorC = createVector(new ArrayList<String>(Arrays.asList(setC)), allLinksArrayList);
		
		double dotProduct = (double) vectorDotProduct(vectorA, vectorB);
		System.out.println("Set A and B :" + (dotProduct  / (singleVectorsum(vectorA) + singleVectorsum(vectorB) - dotProduct)));
		dotProduct = (double) vectorDotProduct(vectorA, vectorC);
		System.out.println("Set A and C :" + (dotProduct  / (singleVectorsum(vectorA) + singleVectorsum(vectorC) - dotProduct)));
		dotProduct = (double) vectorDotProduct(vectorB, vectorC);
		System.out.println("Set B and C :" + (dotProduct  / (singleVectorsum(vectorB) + singleVectorsum(vectorC) - dotProduct)));
		*/
		
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
	
	private static BitSet createVector(ArrayList<String> setX, ArrayList<String> setOfAllTerms) {
		BitSet retVal = new BitSet();
		
		for (int i = 0; i < setOfAllTerms.size(); i++) { //for each term 
			if (setX.contains(setOfAllTerms.get(i))) {
				retVal.set(i, true); // set the ith bit to 1 for the ith term
			} // else skip to next term and leave the nth bit as 0
		}
		return retVal;
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
	
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		  int totalLength = first.length;
		  for (T[] array : rest) {
		    totalLength += array.length;
		  }
		  T[] result = Arrays.copyOf(first, totalLength);
		  int offset = first.length;
		  for (T[] array : rest) {
		    System.arraycopy(array, 0, result, offset, array.length);
		    offset += array.length;
		  }
		  return result;
		}
}
