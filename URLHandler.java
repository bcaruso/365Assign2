/*
*	URLHandler CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*   Handles creating the BTree from a root URL and its first degree links.
*/

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.HttpStatusException;
import java.net.*;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Scanner;

public class URLHandler {
	
		/** 
		*	stopWords	
		*	This array is searched prior to inserting a word into the BTree
		*/	
	
		private static String[] stopWords = new String[]{"a","able","about","across","after","all",
		"almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could",
		"dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers",
		"him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might",
		"most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said",
		"say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they",
		"this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom",
		"why","will","with","would","yet","you","your"};
		
		private static String[] skipLinkPiecesWikipedia = new String[]{"#",".tif", ".tiff", ".gif",".jpeg", ".jpg", ".png", ".pdf"};
		private static Histogram linksSeen = new Histogram(64);
	
		/* STOP WORDS ARRAY DONE */	
	
		public static void createBTree(URL url,int k) throws IOException, URISyntaxException{
			
			String fileName = url.toString().replace("/",""); 
			if(fileName.length() > 256){
				fileName = fileName.substring(0, 256);
			}
			Document doc = Jsoup.parse(url.openStream(),null,url.toURI().toString());
			
			BTree tree = new BTree(fileName,k);
			
			URLConnection urlInfo = url.openConnection();
			urlInfo.connect();
			
			System.out.println("Last Modified: " + LocalDateTime.ofEpochSecond(urlInfo.getLastModified(),0,ZoneOffset.UTC));
			
			Elements links = doc.select("a[href]");

			
			String dirtyDoc = doc.text();
			//Remove Punctuation
			String cleanDoc = dirtyDoc.replaceAll("[^a-zA-Z ]","");

			linksSeen.add(url.toString());
			Scanner s = new Scanner(cleanDoc);
			
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<MAIN PAGE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
			while(s.hasNext()){
				String str = s.next().toLowerCase();
				if(!isStopWord(str)){
					tree.put(str);
				}
			}
			
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<LINKS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
			int numOfLinks = 0;
			
			for (Element link : links){
				if(numOfLinks <= 150){
					if(!wikipediaBadLinkFilter(link.attr("abs:href"))){
						linksSeen.add(link.attr("abs:href"));
						System.out.println(numOfLinks +" : " +link.attr("abs:href"));

						try{
				
						doc = Jsoup.connect(link.attr("abs:href")).get();
				
						dirtyDoc = doc.text();
						//Remove Punctuation
						cleanDoc = dirtyDoc.replaceAll("[^a-zA-Z0-9 ]","");
			
						s = new Scanner(cleanDoc);
			
							while(s.hasNext()){
								String str = s.next().toLowerCase();;
								if(!isStopWord(str)){
									tree.put(str);
								}
							}
						numOfLinks++;

						}catch(UnsupportedMimeTypeException e1){
							System.out.println("BAD MIME TYPE SKIPPED: "+link.attr("abs:href"));
						}catch(HttpStatusException e2){
							System.out.println("PROBLEM FETCHING URL SKIPPED: "+link.attr("abs:href"));
						}catch(SocketTimeoutException e3){
							System.out.println("SKIPPED: "+link.attr("abs:href"));
						}
					
					}
					
				}else{
					break;
				}
			}	
			
			
			
			s.close();
						
		}
		
		public static void createQueryBTree(URL url,int k) throws IOException, URISyntaxException{
			 
			
			Document doc = Jsoup.parse(url.openStream(),null,url.toURI().toString());
			
			BTree tree = new BTree("query",k);
			
			URLConnection urlInfo = url.openConnection();
			urlInfo.connect();
			
			System.out.println("Last Modified: " + LocalDateTime.ofEpochSecond(urlInfo.getLastModified(),0,ZoneOffset.UTC));
			
			Elements links = doc.select("a[href]");

			
			String dirtyDoc = doc.text();
			//Remove Punctuation
			String cleanDoc = dirtyDoc.replaceAll("[^a-zA-Z ]","");
			
			Scanner s = new Scanner(cleanDoc);
			
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<MAIN PAGE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
			while(s.hasNext()){
				String str = s.next().toLowerCase();
				if(!isStopWord(str)){
					tree.put(str);
				}
			}
		
			s.close();
						
		}
		
		private static boolean isStopWord(String key){
			return Arrays.binarySearch(stopWords,key.toLowerCase()) >= 0 ;
		}
		
		private static boolean wikipediaBadLinkFilter(String link){
			if(link.indexOf("wikipedia.org") >=0){
				if(link.indexOf("en.wikipedia.org") < 0){
				System.out.println("PAGE IN DIFFERENT LANGUAGE - SKIPPED: "+link);
				return true;
				}
				
			}
			
			if(linksSeen.contains(link) != null){
				System.out.println("Hey! I've see you before! - SKIPPED: "+link);
				return true;
				
			}
			
			for(int i = 0; i<skipLinkPiecesWikipedia.length; i++){
				if(link.indexOf(skipLinkPiecesWikipedia[i])>=0){
					System.out.println("SKIPPED: "+link);
					linksSeen.add(link);
					return true;
				}
			}
			
			return false;
		}
		
}