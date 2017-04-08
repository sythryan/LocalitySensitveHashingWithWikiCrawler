//Author: Syth Ryan

import java.io.File;

public class MinHashAccuracy {

	public static int calculateMinHashAccuracy(String folder, int numPermutations, double error) {
		MinHash myHasher = new MinHash(folder, numPermutations);
		String[] listOfFileNames = new File(folder).list();
		
		int numOutOfErrorBounds = 0;
		
		for (int i = 0; i < listOfFileNames.length - 1; i++) {
			for (int n = i+1; n < listOfFileNames.length; n++) {
				if (Math.abs(myHasher.exactJaccard(listOfFileNames[i].toString(), listOfFileNames[n].toString()) - 
						myHasher.approximateJaccard(listOfFileNames[i].toString(), listOfFileNames[n])) > error) {
					numOutOfErrorBounds++;
				}
			}
		}
		return numOutOfErrorBounds;
	}
	
		

}
