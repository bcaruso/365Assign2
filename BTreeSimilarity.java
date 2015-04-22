

import java.util.Iterator;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import java.net.*;

public class BTreeSimilarity {
	
		private static double vectorLength(BTree tree){
			
			// Super Cool Functional Approach!		
			BiFunction<Double,SubNode,Double> sumOfSquares = (Double r, SubNode s) -> r + Math.pow(s.count,2);
			return Math.sqrt(tree.reduce(0.0,sumOfSquares));
			
		}	
		
		private static double vectorScalar(BTree b1, BTree b2){
			
			BTree smallestTree;
			BTree largestTree;
			
			//Only go through the smallest Tree.
			if(b1.getNumberOfEntries() > b2.getNumberOfEntries()){
				smallestTree = b2;
				largestTree = b1;
			}else{
				smallestTree = b1;
				largestTree = b2;
			}
			
			// Super Cool Functional Approach!
			BiFunction<Double,SubNode,Double> scalarProduct = (Double r, SubNode e) -> r + (largestTree.get(e.key) * e.count);
			return 	smallestTree.reduce(0.0,scalarProduct);
			
		}
		
		private static double similarMetric(BTree b1, BTree b2){
			
			// This is the result just before taking the arccos to find the angle 
			// between two vectors. Don't want the angle just this ratio.
			return vectorScalar(b1,b2)/(vectorLength(b1) * vectorLength(b2));
			
		}
		
		public static String[] findMostSimilar(String query, ArrayList<BTree> ref) throws IOException, URISyntaxException{
			
			Iterator<BTree> it = ref.iterator();
			
			URL url = new URL(query);
			
			URLHandler.createQueryBTree(url,64);
			
			File f = new File("query");
			RandomAccessFile queryFile = new RandomAccessFile(f,"rw");
			BTree queryBTree = new BTree( queryFile , 64);
			queryBTree.url = url.toString();
			
			String[] mostSimilar;
			double maxSimilarity = 0.0;
			
			List<SimEntry> similarities = new ArrayList<SimEntry>();
			
			while (it.hasNext()){
				BTree refBTree = it.next();
				double simMet = similarMetric(queryBTree,refBTree);
				similarities.add(new SimEntry(refBTree.url,simMet));
				System.out.println(refBTree.url);
				System.out.println("Current Max: "+ maxSimilarity);
				System.out.println("Similarity Result: " + simMet);
				if( maxSimilarity < simMet){
					maxSimilarity = simMet;
				}
			}
			
			Collections.sort(similarities, (o1,o2) -> o1.value.compareTo(o2.value)* -1 );
			mostSimilar = new String[similarities.size()];
			System.out.println(">>>>>>>>>>>>>>> RESULT SUMMARY <<<<<<<<<<<<<<<<");
			for(int i = 0; i< similarities.size(); i++){
				System.out.println(similarities.get(i).key + " : " + similarities.get(i).value);
				mostSimilar[i] = similarities.get(i).key;
			}
			
			f.delete();
			return mostSimilar;
		} 
		
		private static class SimEntry{
			String key;
			Double value;
			
			public SimEntry(String k, Double v){
				key = k;
				value = v;
			}
			
		}
				
}