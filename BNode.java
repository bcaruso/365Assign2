
/*
*
*	The foundation of the BTree - the Node. Contains words (keys), count (value), and
*	pointers to children nodes (long seek offsets).
*
*/

public class BNode {
	
	String [] words;
	int[] count;
	long[] children;
	
	public BNode(int k){
		// k is the number of keys in a Node.
		words = new String[k];
		count = new int[k];
		children = new long[k];
	}	
	
}