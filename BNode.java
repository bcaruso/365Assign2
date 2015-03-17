
/*
*	BNode CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*
*	The foundation of the BTree - the Node. Contains words (keys), count (value), and
*	pointers to children nodes (long seek offsets).
*
*   Strings will be limited at 64 characters.
*/

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

public class BNode {
	
	String[] words;
	int[] count;
	long[] children; // seek positions
	
	public BNode(int k){
		// k is the number of keys in a Node.
		words = new String[k];
		count = new int[k];
		children = new long[k];
	}	
	
	public BNode(byte[] b , int k){
	
		// Creates a BNode from a byte[]
		
		words = new String[k];
		count = new int[k];
		children = new long[k];
		
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.position(0);
		
		for(int i = 0; i < words.length ; i++){
			bb.position(i*64);
			byte[] word = new byte[64];
			bb.get(word);
			words[i] = new String(word);
		}
		
		for(int i = 0; i < count.length ; i++){
			count[i] = bb.getInt();
		}
		
		for(int i = 0; i < children.length ; i++){
			children[i] = bb.getLong();
		}
	}
	
	public byte[] getBytes(){
		
		// Translates a node to a byte Array to Persist
	
		ByteBuffer b = ByteBuffer.allocate(words.length * 64 + count.length * 4 + children.length * 8);
		
		for(int i = 0; i < words.length ; i++){
			b.position(i*64);
			if(words[i] != null){
				b.put(words[i].getBytes());
			}
		}
		
		b.position(words.length*64);
		
		for(int i = 0; i < count.length ; i++){
			b.putInt(count[i]);
		}
		
		for(int i = 0; i < children.length ; i++){
			b.putLong(children[i]);
		}
		
		byte[] bArray = new byte[words.length * 64 + count.length * 4 + children.length * 8];
		b.rewind();
		b.get(bArray);
		
		return bArray;
	}
	
	/*
	
		>>>>> FOR TESTING <<<<<<
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		String[] w = {"Brandon","Joseph",null,"Caruso","Joseph",null};
		words = new String[w.length];
		System.arraycopy(w,0,words,0,w.length);
		
		int[] cnt = {2,4,6,8,10,12};
		count = new int[cnt.length];
		System.arraycopy(cnt,0,count,0,cnt.length);
		
		long[] c = {1,3,5,6,9,11};
		children = new long[c.length];
		System.arraycopy(c,0,children,0,c.length);
				
		RandomAccessFile file = new RandomAccessFile("testBTree","rw");

		file.write(getBytes());
		
		file.seek(0);
		
		int k = 6;
		
		String[] newWords = new String[k];
		int[] newCount = new int[k];
		long[] newChildren = new long[k];
		
		byte[] b = new byte[newWords.length * 64 + newCount.length * 4 + newChildren.length * 8];
		
		file.read(b);
		
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.position(0);
		
		for(int i = 0; i < newWords.length ; i++){
			bb.position(i*64);
			byte[] word = new byte[64];
			bb.get(word);
			newWords[i] = new String(word);
		}
		
		for(int i = 0; i < newCount.length ; i++){
			newCount[i] = bb.getInt();
			
		}
		
		for(int i = 0; i < newChildren.length ; i++){
			newChildren[i] = bb.getLong();
		}
		
		for(int i = 0; i < newWords.length ; i++){
			System.out.println(i +" "+ newWords[i]);
		}
		
		for(int i = 0; i < newCount.length ; i++){
			System.out.println(i +" "+newCount[i]);
		}
		
		for(int i = 0; i < newChildren.length ; i++){
			System.out.println(i +" "+newChildren[i]);
		}
		
	}
	*/
	
}