//Author Syth Ryan

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class PageRank {
	
	static double beta = 0.85;
	
	File graph;
	double epsilon;
	ArrayList<Double> PVector; // could not make generic array with tuple type "[]"
	int maxVertexes;
	Map<String, Tuple2<Integer, ArrayList<String>>> inOutList = new HashMap<String, Tuple2<Integer, ArrayList<String>>>(); //stores incoming links and out link count
	ArrayList<String> allKeys = new ArrayList<String>();
	ArrayList<String> allBaseKeys = new ArrayList<String>();
	
	public PageRank(String fileName, double e) {
		graph = new File(fileName);
		epsilon = e;
		
		try {
			Scanner reader = new Scanner(graph);
			
			maxVertexes = reader.nextInt();
			
			while (reader.hasNextLine()) {
				StringTokenizer lineReader = new StringTokenizer(reader.nextLine());
				while (lineReader.hasMoreTokens()) {
					Tuple2<Integer, ArrayList<String>> newInOut;
					String currentPage = lineReader.nextToken();
					
					if (!allBaseKeys.contains(currentPage)) {
						allBaseKeys.add(currentPage);
					}
					
					if (!allKeys.contains(currentPage)) {
						allKeys.add(currentPage);
					}
					
					if (lineReader.hasMoreTokens()) { // there is a link
						String linkToPage = lineReader.nextToken();
						
						//overall we want to
						// link to page: add link to out list
						// Link in count ++		
					
						if (inOutList.containsKey(currentPage)) { //update current list
							newInOut = inOutList.get(currentPage);
							if (!newInOut._1.contains(linkToPage)) {
								newInOut._1.add(linkToPage);
							}
							inOutList.put(currentPage, newInOut);
						} else { //update with a new list
							ArrayList<String> temp = new ArrayList<String>();
							temp.add(linkToPage);
							newInOut = new Tuple2<Integer, ArrayList<String>>(0,temp);
							inOutList.put(currentPage, newInOut);
						}
						
						//in
						if (inOutList.containsKey(linkToPage)) {
							newInOut = inOutList.get(linkToPage);
							newInOut._0 = newInOut._0 + 1;
							inOutList.put(linkToPage, newInOut);
						} else { 
							newInOut = new Tuple2<Integer, ArrayList<String>>(1, new ArrayList<String>());
							inOutList.put(linkToPage, newInOut);
						}
						
						if (!allKeys.contains(linkToPage)) {
							allKeys.add(linkToPage);
						}							
					}  //  skip sink nodes they will be added if there is a link TO them.
				}
			}
			reader.close();	
			
			//Set P0 to uniform probability vector
			PVector = new ArrayList<Double>();
			ArrayList<Double> PnPlusOneVector = PVector;
			double evenDistribution = 1.0/((double) maxVertexes);
			for (int i = 0; i < maxVertexes; i++) {
				PVector.add(evenDistribution);
			}
			
			double Norm = 0.0;
			int iterationCount = 0;
			boolean converged = false;
			
			while(!converged) {
				PVector = PnPlusOneVector;
				PnPlusOneVector = pnVectorPlusOne(PVector);
				Norm = 0.0;
				for (int i = 0; i < maxVertexes; i++) {
					Norm = Norm + Math.abs(PnPlusOneVector.get(i) - PVector.get(i));
				}
				iterationCount++;
				if (Norm <= epsilon) {
					converged = true;
				}
			}
			System.out.println("epsilon iterations: " + iterationCount);
			
		} catch (FileNotFoundException e1) {
			System.out.print("could not create initial page rank vector");
		}
	}
	
	private ArrayList<Double> pnVectorPlusOne(ArrayList<Double> pnVector) {
		ArrayList<Double> pnPlusOne = new ArrayList<Double>();
		//Initialize Pn+1
		for (int i = 0; i < pnVector.size(); i++) {
			pnPlusOne.add((1.0 - beta)/(double)maxVertexes);
		}

		for (int p = 0; p < allBaseKeys.size(); p++) { // for every page p
			ArrayList<String> setQ = inOutList.get(allBaseKeys.get(p))._1;
			if (setQ.size() > 0) {
				for (int q = 0; q < setQ.size(); q++) { // for every link q
					int index = allBaseKeys.indexOf(setQ.get(q));
					if (index > -1) {
						pnPlusOne.set(index, pnPlusOne.get(index) + beta * pnVector.get(p) / (double)setQ.size());
					}
				}
			} else {
				for (int q = 0; q < maxVertexes; q++) { // for every page q
					pnPlusOne.set(q, pnPlusOne.get(q) + beta * pnVector.get(p) / (double)maxVertexes);
				}
			}
		}
		return pnPlusOne;
	}
	
	public double pageRankOf(String vertex) {
		int index = allBaseKeys.indexOf(vertex);
		if (index > -1) {
			return PVector.get(index);
		} else {
			return 0.0;
		}
	}
	
	public int outDegreeOf(String vertex) {
		return inOutList.get(vertex)._1.size();
	}
	
	public int inDegreeOf(String vertex) {
		return inOutList.get(vertex)._0;
	}
	
	public int numEdges() {
		int count = 0;
		
		for (int i = 0; i < allKeys.size(); i++) {
			count = count + inOutList.get(allKeys.get(i))._0;
		}
		return inOutList.size();
	}
	
	public String[] topKPageRank(int k) {
		WeightedQ theQ = new WeightedQ();
		String[] retVal = new String[k];
		
		for(int i = 0; i < allBaseKeys.size(); i++) {
			theQ.Add(new Tuple2<String, Double> (allBaseKeys.get(i), PVector.get(allBaseKeys.indexOf(allBaseKeys.get(i)))));
		}
		
		String extractedVal;
		
		for(int i = 0; i < k; i++) {
			extractedVal = theQ.Extract();
			if (extractedVal == null) {
				break;
			}
			retVal[i] = extractedVal;
		}
		return retVal;
	}
	
	public String[] topKInDegree(int k) {
		WeightedQ theQ = new WeightedQ();
		String[] retVal = new String[k];
		
		for(int i = 0; i < allKeys.size(); i++) {
			theQ.Add(new Tuple2<String, Double> (allKeys.get(i), (double)inOutList.get(allKeys.get(i))._0));
		}
		
		String extractedVal;
		
		for(int i = 0; ((extractedVal = theQ.Extract()) != null) & (i < k); i++) {
			retVal[i] = extractedVal;
		}
		return retVal;
	}
	
	public String[] topKOutDegree(int k) {
		WeightedQ theQ = new WeightedQ();
		String[] retVal = new String[k];
		
		for(int i = 0; i < allBaseKeys.size(); i++) {
			theQ.Add(new Tuple2<String, Double> (allBaseKeys.get(i), (double)inOutList.get(allBaseKeys.get(i))._1.size()));
		}
		
		String extractedVal;
		
		for(int i = 0; ((extractedVal = theQ.Extract()) != null) & (i < k); i++) {
			retVal[i] = extractedVal;
		}
		return retVal;
	}
}
