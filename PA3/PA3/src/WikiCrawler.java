//Author Syth Ryan

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class WikiCrawler {
	
	static final String BASE_URL = "http://web.cs.iastate.edu/%7Epavan";
	String seed;
	String[] topics;
	int maxPages;
	String theFileName;
	boolean weighted;
	
	String[] seedList;
	
	WeightedQ theQ;
	Map<String, Boolean> visited;
	Map<String, Boolean> Disallowed;
	
	public WikiCrawler(String seedURL, String[] keywords, int max, String fileName, boolean isWeighted) {
		topics = keywords;
		maxPages = max;
		theFileName = fileName;
		weighted = isWeighted;
		seed = seedURL;
		
		theQ = new WeightedQ();
		visited = new HashMap<String, Boolean>();
		Disallowed = robotstxt(BASE_URL);
	}
		
	public void crawl() {
		// crawl up to max pages
		String url = seed;
		try {
			File myFile = new File(theFileName);
			
			// overwrite if the file exists
			if (myFile.exists()) {
				myFile.delete();
			}
			myFile.createNewFile();
			
			PrintWriter myWriter = new PrintWriter(myFile);
			
			myWriter.println(maxPages);
			
			for (int i = 0; (i < maxPages); i++) {
				Map<String, Boolean> edgesReported = new HashMap<String, Boolean>();
				try {
					if (url == null) {
						break;
					}
					URL myURL = new URL(BASE_URL + url);
					URLConnection Connection = myURL.openConnection();
					
					InputStreamReader myInputReader = new InputStreamReader(Connection.getInputStream());
					BufferedReader myBufferReader = new BufferedReader(myInputReader);
					
					visited.put(url, true);
					
					String inputLine;
					String pageSource = "";
					
					while ((inputLine = myBufferReader.readLine()) != null) {
						pageSource = pageSource + inputLine;
					}
					
					pageSource = pageSource.substring(pageSource.indexOf("<p>"));
					
					if (!pageSource.contains("<a")) {
						myWriter.print(url);
					} else {
						
						String remaining = pageSource;
						while(remaining.contains("<a")) {
							String link = remaining.substring(remaining.indexOf("<a"), remaining.indexOf("</a>"));
							link = link.substring(link.indexOf('"') + 1);
							link = link.substring(0,link.indexOf('"'));
							if (!link.contains("#") & 
							    !link.contains(":") & 
							    link.startsWith("/wiki/") &
							    !Disallowed.containsKey(link) &
								!link.equals(url) &
								!edgesReported.containsKey(link)){
								
								edgesReported.put(link, true);
								
								if (!visited.containsKey(link)) {
									visited.put(link, true);
									if (weighted) {
										theQ.Add(new Tuple2<String, Double>(link, weighLink(link, pageSource, topics)));
									} else {
										theQ.Add(new Tuple2<String, Double>(link, 0.0));
									}
								}
								System.out.println(url);
								myWriter.println(url + "   " + link);
							}
							remaining = remaining.substring(remaining.indexOf("</a>") + 4);
						}
					}
					myBufferReader.close();
					myInputReader.close();
						
					} catch (IOException e) {
						i--;
					}
					url = theQ.Extract();
					
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						System.out.println("couldn't sleep");
					}
				}
				
			myWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, Boolean> robotstxt(String theBaseURL) {
		Map<String, Boolean> theMap = new HashMap<String, Boolean>();
		try {
			URL myURL = new URL(theBaseURL + "/robots.txt");
			URLConnection Connection = myURL.openConnection();
			
			InputStreamReader myInputReader = new InputStreamReader(Connection.getInputStream());
			BufferedReader myBufferReader = new BufferedReader(myInputReader); 
			
			String inputLine;
			while ((inputLine = myBufferReader.readLine()) != null) {
				if (inputLine.contains("Disallow: ")) {
					inputLine = inputLine.substring(inputLine.indexOf("Disallow: ") + 10).trim();
					theMap.put(inputLine, true);
				}
			}
			
			myInputReader.close();
			myBufferReader.close();
		} catch (IOException e) {
			System.out.println("robots.txt failed");
		}
		return theMap;
	}
	
	/* Adds a weight to a link, if weighted otherwise weight is set to 0 */
	private double weighLink(String link, String pageSource, String[] topics) {
		String insensitivePageSource = pageSource.toLowerCase();
		if (weighted) {
			int min = 21;
			for (int i = 0; i < topics.length; i++) {
				if (link.contains(topics[i])) {
					return 1.0;
				} else {
					if (pageSource.contains(topics[i])) {
						int topicIndex = 0;
						int linkIndex = 0;
						
						StringTokenizer traverser = new StringTokenizer(insensitivePageSource);
						for (int t = 0; ((topicIndex == 0 || linkIndex == 0) & traverser.hasMoreTokens()); t++) {
							StringTokenizer topicTraverser = new StringTokenizer(topics[i]);							
							String current = "";
							
							for (int j = 0; ((j < topicTraverser.countTokens()) & (traverser.hasMoreTokens())); j++) {
								current = current + traverser.nextToken().toLowerCase().replaceAll("_", "");
							}
							
							if (current.contains(topics[i].toLowerCase().replaceAll(" ", ""))) {
								topicIndex = t;
							}
							if (current.contains(link.toLowerCase().replaceAll("_", ""))) {
								linkIndex = t;
								
							}
						}
						
						if (Math.abs(topicIndex - linkIndex) > 20) {
							return 0.0;
						} else {
							min = Integer.min(min, Math.abs(topicIndex - linkIndex));
						}
					} else {
						return 0.0;
					}
				}
			}
			return 1.0 / (min + 2.0);
		} else {
			return 0.0;
		}
	}
}