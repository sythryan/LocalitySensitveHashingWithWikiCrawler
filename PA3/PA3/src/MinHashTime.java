//Author: Syth Ryan

import java.io.File;

public class MinHashTime {
	String Globalfolder;
	int globalNumPermutations;

	public MinHashTime(String folder, int numPermutations) {
		Globalfolder = folder;
		globalNumPermutations = numPermutations;
	}
	
	public void Timer() {
		MinHash myHasher = new MinHash(Globalfolder, globalNumPermutations);
		String[] listOfFileNames = new File(Globalfolder).list();
		long time;
		
		time = System.currentTimeMillis();	
		myHasher.minHashMatrix();
		for (int i = 0; i < listOfFileNames.length - 1; i++) {
			for (int n = i+1; n < listOfFileNames.length; n++) {
				myHasher.exactJaccard(listOfFileNames[i].toString(), listOfFileNames[n].toString());
			}
		}
		System.out.println("Computing exact Jaccard took about " + Long.toString((System.currentTimeMillis() - time) / 1000) + " seconds.");
		
		time = System.currentTimeMillis();
		myHasher.minHashMatrix();
		System.out.println("Computing MinHash matrix took about " + Long.toString((System.currentTimeMillis() - time) / 1000) + " seconds.");
		
		time = System.currentTimeMillis();
		myHasher.minHashMatrix();
		for (int i = 0; i < listOfFileNames.length - 1; i++) {
			for (int n = i+1; n < listOfFileNames.length; n++) {
				myHasher.approximateJaccard(listOfFileNames[i].toString(), listOfFileNames[i + 1].toString());
			}
		}
		System.out.println("Computing approximate Jaccard took about " + Long.toString((System.currentTimeMillis() - time) / 1000) + " seconds.");
	}
}
