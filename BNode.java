
/*
*	BNode CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*
*	The foundation of the BTree - the Node. Contains words (keys), count (value), and
*	pointers to children nodes (long seek offsets).
*
*
*	
*	[  key  ] [  key  ]     [  key  ] [  key  ] │
*	[ count ] [ count ] ...	[ count ] [ count ] │─ BNode
*	[ child ] [ child ]     [ child ] [ child ] │
*	───────── ─────────     ───────── ─────────
*	 SubNode   SubNode		 SubNode	SubNode
*
*   Strings will be limited at 64 characters.
*/

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.BiFunction;

public class BNode {
	
	SubNode[] entries;
	boolean isLeaf;
	int numberOfKeys;
	long position;
	
	public BNode(int k){
		// k is the number of keys in a Node.
		entries = new SubNode[k];
		
		for(int i = 0; i < entries.length ; i++){
			entries[i] = new SubNode();
		}		
		
		isLeaf = true;
		
	}	
	
	public BNode(byte[] b , int k){
	
		// Creates a BNode from a byte[]
		
		entries = new SubNode[k];
		
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.position(0);
		
		long childSum = 0;
				
		for(int i = 0; i < entries.length ; i++){
			byte[] entry = new byte[64+4+8];
			bb.get(entry);
			entries[i] = new SubNode(entry);
			if(!entries[i].key.isEmpty()){
				numberOfKeys++;
			}
			childSum += entries[i].child;
		}
		isLeaf = childSum == 0;
	}
	
	public byte[] getBytes(){
		
		// Translates a node to a byte Array to Persist
	
		ByteBuffer b = ByteBuffer.allocate(entries.length * ( 64 + 4 +  8) );
		
		b.position(0);
		
		for(int i = 0; i < entries.length ; i++){
			if(entries[i] != null){
				b.put(entries[i].getBytes());
			}else{
				b.position(b.position() + ( 64 + 4 +  8) );
			}
		}
		
		byte[] bArray = new byte[entries.length * ( 64 + 4 +  8)];
		b.rewind();
		b.get(bArray);
		
		return bArray;
	}
	
	public void add(String k){
		int containsKey = contains(k);

		if(containsKey >= 0){
			// Hey! I've seen this before.
			entries[containsKey].count++;
		}else{
			// This is a New Key
			if(numberOfKeys < entries.length-1){
				entries[numberOfKeys+1].key = entries[numberOfKeys].key ;
				entries[numberOfKeys+1].count = entries[numberOfKeys].count;
				entries[numberOfKeys+1].child = entries[numberOfKeys].child;
			}
			
			entries[numberOfKeys].key = k;
			entries[numberOfKeys].count++;
			numberOfKeys++;

			Arrays.sort(entries, (SubNode o1, SubNode o2) -> {
				
				if (o1 == null && o2 == null) {
					return 0;
				}
			
				if (o1 == null) {
					return 1;
				}
			
				if (o2 == null) {
					return -1;
				}
				
				return o1.compareTo(o2);
			});
	
		}
	}
	
	public void forEach(Consumer<SubNode> action){
		for(int i = 0; i < numberOfKeys; i++ ){
			action.accept(entries[i]);
		}	
	}
	
	public Double reduce(Double initial , BiFunction<Double,SubNode,Double> action){
		Double result = initial;
		
		for(int i = 0; i< numberOfKeys; i++ ){
			result = action.apply(result,entries[i]);
		}
		
		return result;
	}
	
	public int contains(String key){
		int contains = -1;
		for(int i = 0; i< entries.length; i++ ){
			if(entries[i].key.equals(key)){
				contains = i;
				break;
			}
		}
		return contains;
	}
	
}