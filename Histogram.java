/*
*	HISTOGRAM CLASS - CSC 365 - Assignment 1
*
*	Author: Brandon Caruso
*	A hash map containing Strings and the Frequency of that String
*	
*/

import java.util.function.Consumer;
import java.util.function.BiFunction;
import java.net.*;

public class Histogram {


	private double loadFactor = .75;
	private int numberOfEntries = 0;
	private URL url;
	private Entry[] histogram;
	
	public Histogram(int size){
		histogram = new Entry[size];
	}
	
	public Histogram(URL u){
		url = u;
		histogram = new Entry[16];
	}
	
	public Histogram(URL u, int size){
		url = u;
		histogram = new Entry[size];
	}
	
	public Histogram(URL u, int size, float loadF ){
		url = u;
		histogram = new Entry[size];
		loadFactor = loadF;
	}
	
	public int getCount(String key){
		Entry contains = this.contains(key);
		
		if( contains == null ){
			// Umm... I can find any thing
			return 0;
		}else{
			// Give me your Count
			return contains.getCount();
		}
		
	}
	
	public void add(String key){
		
		// Have I seen this before?
		Entry contains = this.contains(key);
		
		if(contains == null){
			// NOPE!
			addEntry(new Entry(key));
		}else{
			//YEP!
			contains.incCount();
		}
		
		if(((double) numberOfEntries/histogram.length) > .75){
			// Well we knew it had to happen sometime ... TIME TO REHASH!
			rehash();
		}	
	}
	
	private void addEntry(Entry e){
		int eHash = e.getHashCode();
		int index = Math.abs(eHash) % histogram.length;
		Entry current = histogram[index];
		e.next = current;
		histogram[index] = e;
		numberOfEntries += 1;
	}
	
	private void rehash(){
		//System.out.println("Rehashing...");
		// A new Histogram
		Histogram newHistogram = new Histogram(url,histogram.length * 2);
		
		// Copy the Entries over!
		Consumer<Entry> addToNewHistogram = (Entry e) -> newHistogram.addEntry(e);
		this.forEach(addToNewHistogram);
		
		// Replace the old with the new
		Entry[] newArray = newHistogram.getHistogram();
		this.histogram = new Entry[newArray.length];
		
		System.arraycopy(newArray,0,this.histogram,0,newArray.length);
		numberOfEntries = newHistogram.getNumberOfEntries();
	}
	
	private Entry[] getHistogram(){
		return histogram;
	}

	
	public Entry contains(String key){
		int eHash = key.toLowerCase().hashCode();
		int index = Math.abs(eHash) % histogram.length;
		
		Entry current = histogram[index];
		
		while(current != null && !current.getKey().equals(key.toLowerCase())){
			current = current.next;
		}
		
		return current;
	}
	
	public int getNumberOfEntries(){
		return numberOfEntries;
	}
	
	public void printOut(){
		Consumer<Entry> printEntry = (Entry e) -> System.out.println("Key: " + e.getKey() + " Count: " + e.getCount()); 
		this.forEach(printEntry);
	}

	public void forEach(Consumer<Entry> action){
		for(int i = 0; i<histogram.length; i++ ){
			Entry current = histogram[i];
			while(current != null){
				Entry nextNode = current.next;
				action.accept(current);
				current = nextNode;
			} 
		}
	}
	
	public Double reduce(Double initial , BiFunction<Double,Entry,Double> action){
		Double result = initial;
		
		for(int i = 0; i<histogram.length; i++ ){
			Entry current = histogram[i];
			while(current != null){
				result = action.apply(result,current);
				current = current.next;
			} 
		}
		
		return result;
	}
	
	public URL getURL(){
		return url;
	}
}