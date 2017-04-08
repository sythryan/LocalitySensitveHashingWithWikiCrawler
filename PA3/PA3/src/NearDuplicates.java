//Author: Syth Ryan

import java.io.File;
import java.util.ArrayList;

public class NearDuplicates {

	public ArrayList<String> nearDuplciateDetector(String folder, int numPermutations, double s, String docName) {
		MinHash myMinHash = new MinHash(folder, numPermutations);
		int[][] minHashMatrix = MinHash.minHashMatrix;
		String[] docNames = new File(folder).list();

		int bands = numPermutations / 5;
		if (bands < 2) {
			bands = 2;
		}
		
		LSH myLSH = new LSH(minHashMatrix, docNames, bands);
		
		ArrayList<String> nearDuplicates = myLSH.nearDuplicatesOf(docName);
		ArrayList<String> result = new ArrayList<String>();
		
		for (int i = 0; i < nearDuplicates.size(); i++) {
			if (1 - myMinHash.approximateJaccard(docName, nearDuplicates.get(i)) < (1 - s)) {
				result.add(nearDuplicates.get(i));
			}
		}
		return result;
	}
}
