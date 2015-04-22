/*
*	DEAR FUTURE BRANDON, 
*
*	Wow, your really looking at this again! This is a BTree, a Webcahe, and a compares 
*	different Persistent (stored on disk) BTree's using a similarity metirc  
*	(cosine similarity). In the past you decided to represent BNode's as Arrays of SubNode
*	ASCII illustrations can be found on those pages. The algorithm for insert you used is 
*	describe in Introduction to Algorithms by Cormen, Leiserson, Rivest, Stein. Let's talk 
*	about the Tree you only had to do insert so hopefully your not looking to this for any 
*	help on remove( if so say a pray and May the Force be with you). Insert can be broken 
* 	down into three chucks insert at root, insert not full, and split. These are the three 
*	main methods that comprise <PUT>. First if root is not full just insert non full. What
* 	that does is adds to the end of the nodes subnode array and then sorts. Becareful 
*	though you want to check if it this node should split before you add. What split does 
*	is neat. The parent node is the node you will add to. You create a new empty node and 
*	find the middle of the child node. Any thing to the right is put in the new Node and 
*	then the middle subnode is pushed up to the parent. The child are taken care of by 
*	putting the child node as the child that is pointed to by the promoted subnode and 
*	then the new node is the child of the next subnode in the parent. Hopefully all that 
*	helps a little look down below, you have done this once you can do it again. Your web 
*	cache used open addressing to try to fill as much of the file as possible(probably 
*   could be improved). The url is hashed and that is translated into a location in the 
*   file if that location is empty you add it there else you keep going to the next 
*   available spot in the file until you should rehash(this is what could be improved).
*   This was slow run but I am sure the computers down the road can handle it. The cache 
*   was to see if the page you added needed to be refreshed and since they are wikipedia 
*   pages they do. Oh by the way the whole point of this assignment was to give the top 
*	three most similar root pages( Wikipedia pages) compared to an inputed url. Overall 
*	this assignment took you a while, but not too long and it also was good for you I 
*	think... Hope the future is meeting your past expectations.
*   
*	Good Luck and Nice Talking to You,
*
*	PAST BRANDON
*
*
*
*	BTree CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*
*   
*/

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.function.Consumer;
import java.util.function.BiFunction;

public class BTree {
	
	String url; //For Anyone who wants it;
	
	BNode root;
	int k;
	long rootPos;
	RandomAccessFile file;
	
	public BTree(String url, int k) throws IOException, FileNotFoundException{
		this.k = k;
		root = new BNode(k);
		file = new RandomAccessFile(url,"rw");
		file.setLength(0);
		file.seek(0);
		
		// Write some basic information
		file.write(url.getBytes());
		file.seek(256);
		file.writeLong(file.getFilePointer()+8);
		root.position = file.getFilePointer();
		rootPos = file.getFilePointer();
		file.write(root.getBytes());
		root.isLeaf = true;
	}
	
	public BTree(RandomAccessFile f, int k) throws IOException, FileNotFoundException{
		this.k = k; 

		file = f;
		file.seek(0);
		
		byte[] url = new byte[256];
		file.read(url);
		
		long rootIndex = file.readLong();
		file.seek(rootIndex);

		byte[] treeBytes = new byte[k * (64+4+8)] ;
		file.read(treeBytes);
		root = new BNode(treeBytes,k);
		root.position = rootIndex;
	}
	
	public void put(String key) {

		BNode oldRoot = root;
		long oldRootPos = rootPos;

		if(oldRoot.numberOfKeys == oldRoot.entries.length - 1 && oldRoot.contains(key)<0){
			// Root Full
			BNode newRoot = allocateBNode();
			root = newRoot;
			newRoot.isLeaf = false;
			newRoot.numberOfKeys = 0;
			updateRootPos();
			newRoot.entries[0].child = oldRootPos;
			splitChild(newRoot,0);
			insertNonFull(newRoot,key);
		}else{
			// Root Not Full
			insertNonFull(oldRoot, key);
		}
		
	}
	
	public int get(String key) {
		return search(root,key);
	}
	
	public int size(){
		return 0;
	}
	
	public boolean contains(String key) {
		return get(key) != 0;
	}
	
	public void forEach(Consumer<SubNode> action){
		forEach(root,action);
	}
	
	private void forEach(BNode node,Consumer<SubNode> action){
		if(!node.isLeaf){
			for(int i = 0; i < node.entries.length; i++){
				if(node.entries[i].child != 0)
					forEach(readNode(node.entries[i].child),action);
			}
		}
		node.forEach(action);
	}
	
	public Double reduce(Double initial , BiFunction<Double,SubNode,Double> action){
		return reduce(root, initial, action);
	}
	
	private Double reduce(BNode node, Double initial , BiFunction<Double,SubNode,Double> action){
		Double result = initial;
		if(!node.isLeaf){
			for(int i = 0; i < node.entries.length; i++){
				if(node.entries[i].child != 0)
					result = reduce(readNode(node.entries[i].child),result,action);
			}
		}
	
		return node.reduce(result,action);
	}
	
	public int getNumberOfEntries(){
		return getNumberOfEntries(root);
	}
	
	private int getNumberOfEntries(BNode b){
		int result = 0;
		if(!b.isLeaf){
			for(int i = 0; i < b.entries.length; i++){
				if(b.entries[i].child != 0)
				  result += getNumberOfEntries(readNode(b.entries[i].child));
			}
		}
		
		result += b.numberOfKeys;
	
		return result;
	}
	
	private void updateRootPos(){
		try{
			rootPos = file.length() - k*(64+4+8);
			file.seek(256);
			file.writeLong(rootPos);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void writeNode(BNode n, long p) {
		try{
			file.seek(p);
			file.write(n.getBytes());
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private BNode readNode(long p) {
		try{
			file.seek(p);
			byte[] treeBytes = new byte[k * (64+4+8)] ;
			file.read(treeBytes);
			BNode b =  new BNode(treeBytes,k);
			b.position = p;
			return b;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	private BNode allocateBNode(){
		try{
			BNode b = new BNode(k);
			long p = file.length();
			writeNode(b,p);
			b.position = p;
			return b;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	private void insertNonFull(BNode b, String key){
		if(b.isLeaf){
			b.add(key);
			writeNode(b,b.position);
		}else{
			int i = 0;
			int contains = b.contains(key);
			if(contains >= 0){
				b.entries[contains].count++;
				writeNode(b,b.position);
			}else{
				while(i < b.numberOfKeys && key.compareTo(b.entries[i].key) > 0){
					i++;
				}
				BNode child = readNode(b.entries[i].child);
			
				if(child.numberOfKeys == child.entries.length - 1 && child.contains(key) < 0){
					splitChild(b, i);
					if(key.compareTo(b.entries[i].key) > 0){
						i ++;
					}
				}
				
				BNode next = readNode(b.entries[i].child);

				insertNonFull(next, key);
			}
			
		}
	}
	
	private void splitChild(BNode b, int c){
	
		BNode newNode = allocateBNode();
		long newNodePosition = newNode.position;
	
		BNode child = readNode(b.entries[c].child);
		long childPosition = b.entries[c].child;
		
		
		BNode addToNode = b;
		
	
		newNode.isLeaf = child.isLeaf;
		newNode.numberOfKeys = 0;
		int medianIndex = k/2 - 1;
		
		for(int i = 0; i <= medianIndex; i++){

			newNode.entries[i].key = child.entries[i+medianIndex+1].key;
			newNode.entries[i].count = child.entries[i+medianIndex+1].count;
			newNode.entries[i].child = child.entries[i+medianIndex+1].child;
			newNode.numberOfKeys++;
		
			child.entries[i+medianIndex+1].key ="";
			child.entries[i+medianIndex+1].count = 0;
			child.entries[i+medianIndex+1].child = 0;
			child.numberOfKeys--;
		}
		
		addToNode.add(child.entries[medianIndex].key);
		int newIndex = addToNode.contains(child.entries[medianIndex].key);
		
		addToNode.entries[newIndex].count = child.entries[medianIndex].count;

		
		child.entries[medianIndex].key = "";
		child.entries[medianIndex].count = 0;
		
		child.numberOfKeys--;
		
		addToNode.entries[newIndex].child = childPosition; 
		addToNode.entries[newIndex+1].child = newNodePosition;
	
		writeNode(child,childPosition);
		writeNode(newNode,newNodePosition);
		writeNode(addToNode,addToNode.position);
	
	}
	
	
	
	private int search(BNode b, String key) {
		
		int i = 0;
		
		while(i < b.numberOfKeys && key.compareTo(b.entries[i].key) > 0){
			i++;
		}
		
		if(i < b.numberOfKeys && key.equals(b.entries[i].key)){
			return b.entries[i].count;
		}else if (b.isLeaf){
			return 0;
		}else{
			return search(readNode(b.entries[i].child),key);
		}
		
	}
}