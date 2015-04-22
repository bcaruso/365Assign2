/*
*	WebCache CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*   
*  	[ [TIME ADDED][BTREE FILENAME] ][ [TIME ADDED][BTREE FILENAME] ]...[ [TIME ADDED][BTREE FILENAME] ]
*   	8 bytes		 256 bytes
*/

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.io.File;
import java.util.Scanner;
import java.net.URL;

public class Loader {
	public static void main(String[] args) throws URISyntaxException, IOException {
			// Create Web Cache
			WebCache c = new WebCache(32);
			File initURLs = new File(args[0]);
			
			Scanner s = new Scanner(initURLs);
			while(s.hasNext()){
				URL url = new URL(s.nextLine());
				c.put(url);
				URLHandler.createBTree(url,64);
			}
			
			s.close();
	}
}