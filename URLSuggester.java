
import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.net.URISyntaxException;
import java.io.*;
import java.net.*;

public class URLSuggester {

		public static WebCache cache;
		public static ArrayList<BTree> referenceTrees = new ArrayList<BTree>(); 
	
		public static void main(String[] args) throws IOException, URISyntaxException{
			
			cache = new WebCache();
			
			// Get Reference URL Files
			File initURLs = new File(args[0]);
			
			Scanner s = new Scanner(initURLs);
			while(s.hasNext()){
				URL url = new URL(s.nextLine());
				//Check if Need to be updated
				long locationToChange = cache.needToChange(url);
				if(locationToChange > 0){
					System.out.println("Refreshing..."+ url);
					File f = new File(url.toString().replace("/",""));
					f.delete();
					URLHandler.createBTree( url , 64);
					cache.updateTime(url);	
				}
				
				String fileName = url.toString().replace("/",""); 
					
				if(fileName.length() > 256){
					fileName = fileName.substring(0, 256);
				}
				
				RandomAccessFile f = new RandomAccessFile(fileName,"rw");
				BTree b = new BTree( f , 64);
				b.url = url.toString();
				referenceTrees.add(b);
				
				
			}
			
			s.close();
			
			
			
			// Initialize gui	
			URLSuggesterGUI gui = new URLSuggesterGUI();
			
			// Establish Event Dispatch Thread and Create GUI
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					gui.createGUI();
				}
			});
			
		}
		
}