//Author: Syth Ryan

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class LSH {	
	private static ArrayList<ArrayList<ArrayList<String>>> tHashTables;
	int rows;
	int globalBands;
	
	public LSH(int[][] minHashMatrix, String[] docNames, int bands) {
		tHashTables = new ArrayList<ArrayList<ArrayList<String>>>();
		rows = minHashMatrix[0].length / bands;	
		globalBands = bands;
		//initialize bands
		for (int n = 0; n < bands; n++) {
			 tHashTables.add(new ArrayList<ArrayList<String>>());
		}
		
		for (int i = 0; i < minHashMatrix.length; i++) { // for each signature			
			for (int n = 0; n < bands; n++) { // for each band
				int fromIndex = n * rows;
				int toIndex = fromIndex + rows;
				
				if (toIndex > minHashMatrix[i].length) {
					toIndex = minHashMatrix[i].length;
				}
				
				int[] tempNBand = Arrays.copyOfRange(minHashMatrix[i], fromIndex, toIndex);
				
				boolean found = false;
				for (int j = 0; j < tHashTables.get(n).size(); j++) {
					if (tHashTables.get(n).get(j).get(0).equals(Arrays.toString(tempNBand))) {
						found = true;
						tHashTables.get(n).get(j).add(docNames[i]);
						break;
					}
				}
				if (found == false) {
					ArrayList<String> tempStringList = new ArrayList<String>();
					tempStringList.add(Arrays.toString(tempNBand)); //first element in the string list is the "key" which is a string of the band signature
					tempStringList.add(docNames[i]);
					tHashTables.get(n).add(tempStringList);
				}
				found = false;
			}
		}
	}

	public ArrayList<String> nearDuplicatesOf(String docName) {
		ArrayList<String> similarDocuments = new ArrayList<String>();
		boolean found = false; // removes unnecessary cycles
		//go through each hash table and accumulate documents that are placed in the same list per hash table
		for (int i = 0; i < tHashTables.size(); i++ ) { // each band
			for (int n = 0; n < tHashTables.get(i).size(); n++) { // for each signature
				for (int j = 0; j < tHashTables.get(i).get(n).size(); j++) {
					if (tHashTables.get(i).get(n).get(j).equals(docName)) {
						ArrayList<String> temp = tHashTables.get(i).get(n);
						temp.remove(0); // remove the signature to add all documents
						similarDocuments.addAll(temp);
						similarDocuments = new ArrayList<String>(new LinkedHashSet<String>(similarDocuments)); // get rid of duplicates
						found = true;
						break;
					}
				}
				if (found == true) { // removes unnecessary cycles
					break;
				}
			}
			found = false;
		}
		return similarDocuments;
	}
}
