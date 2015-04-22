/*
*	SubNode CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*
*	[  key  ]│
*	[ count ]│── SubNode
*	[ child ]│
*
*   Strings will be limited at 64 characters.
*/


import java.nio.ByteBuffer;

public class SubNode implements Comparable<SubNode> {
	
	String key;
	int count;
	long child; // seek positions
	
	public SubNode(){
		key = "";
		count = 0;
		child = 0;
	}
	
	public SubNode(byte[] b){
		
		// Translates a node to a byte Array to Persist
	
		ByteBuffer bb = ByteBuffer.wrap(b);
		
		bb.position(0);
		
		byte[] readKey = new byte[64];
		bb.get(readKey);
		String paddedKey = new String(readKey);
		key = paddedKey.trim();
				
		count = bb.getInt();
		
		child = bb.getLong();
	}	
	
	public byte[] getBytes(){
		
		// Translates a node to a byte Array to Persist
	
		ByteBuffer b = ByteBuffer.allocate( 64 + 4 +  8);
		if(key.length() <= 64){
			b.put(key.trim().getBytes());
		}else{
			b.put(key.substring(0,65).trim().getBytes());
		}
		b.position(64);
		b.putInt(count);
		b.putLong(child);
		
		byte[] bArray = new byte[64 + 4 + 8];
		b.rewind();
		b.get(bArray);
		
		return bArray;
	}
	
	public int compareTo(SubNode o){
		
		if(key.isEmpty() && !o.key.isEmpty()){
			return 1;
		}
		
		if(!key.isEmpty() && o.key.isEmpty()){
			return -1;
		}
		
		return this.key.compareTo(o.key);
	}
	
}