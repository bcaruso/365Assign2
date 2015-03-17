
/*
*	BTree CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*
*   
*/

import java.io.RandomAccessFile;

public class BTree {
	
	BNode root;
	BNode current;
	RandomAccessFile file;
	
	public BTree(int k){
		root = new BNode(k);
		file = new RandomAccessFile("testBTree","rw");
		writeNode(root);
	}
	
	public BTree(RandomAccessFile f){
		file = f;
	}
	
	public void put(String key){
		
	}
	
	public int get(String key){
		return 0;
	}
	
	public int size(){
		return 0;
	}
	
	public boolean contains(String key){
		return false;
	}
	
	public void forEach(){
	
	}
	
	public void reduce(){
	
	}
	
	private void writeNode(BNode n){
		
	}
	
	private BNode readNode(){
		return null;
	}
	
}