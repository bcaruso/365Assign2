/*
*	URLHandler CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*   Handles creating the BTree from a root URL and its first degree links.
*/

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.net.*;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.io.IOException;
import java.net.URISyntaxException;
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
	
		/* STOP WORDS ARRAY DONE */	
	
		public static void createBTree(URL url) throws IOException, URISyntaxException{
						
			Document doc = Jsoup.parse(url.openStream(),null,url.toURI().toString());
			
			URLConnection urlInfo = url.openConnection();
			urlInfo.connect();
			
			System.out.println("Last Modified: " + LocalDateTime.ofEpochSecond(urlInfo.getLastModified(),0,ZoneOffset.UTC));
			
			Elements links = doc.select("a[href]");

			
			String dirtyDoc = doc.text();
			//Remove Punctuation
			String cleanDoc = dirtyDoc.replaceAll("[^a-zA-Z0-9 ]","");
			//System.out.println(cleanDoc);
			
			Scanner s = new Scanner(cleanDoc);
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<MAIN PAGE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			while(s.hasNext()){
				String str = s.next();
				if(!isStopWord(str))
					System.out.println(str);
			}
			
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<LINKS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

			
			for (Element link : links){
				System.out.println(link.attr("abs:href"));
				doc = Jsoup.connect(link.attr("abs:href")).get();
				
				dirtyDoc = doc.text();
				//Remove Punctuation
				cleanDoc = dirtyDoc.replaceAll("[^a-zA-Z0-9 ]","");
				//System.out.println(cleanDoc);
			
				s = new Scanner(cleanDoc);
			
				while(s.hasNext()){
					String str = s.next();
					if(!isStopWord(str))
						System.out.println(str);
				}
			}	
			
			
			
			s.close();
						
		}
		
		private static boolean isStopWord(String key){
			return Arrays.binarySearch(stopWords,key.toLowerCase()) >= 0 ;
		}
		
		public static void main(String[] args)  throws IOException, URISyntaxException {
			createBTree(new URL(args[0]));
		}
}