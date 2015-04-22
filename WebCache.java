/*
*	WebCache CLASS - CSC 365 - Assignment 2
*
*	Author: Brandon Caruso
*   
*  	[ [TIME ADDED][BTREE FILENAME] ][ [TIME ADDED][BTREE FILENAME] ]...[ [TIME ADDED][BTREE FILENAME] ]
*   	8 bytes		 256 bytes
*/

import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

import java.net.URLConnection;
import java.net.URL;

public class WebCache {
	
	File cacheFile;
	RandomAccessFile cache;
	int size;
	
	public WebCache(int s){
		try{
			cacheFile = new File("cache");
			cache = new RandomAccessFile(cacheFile,"rw");
			cache.setLength(s*(8+256));
			size = s;
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public WebCache(){
		try{
			cacheFile = new File("cache");
			cache = new RandomAccessFile(cacheFile,"rw");
			size = (int)cache.length()/(8+256);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public void put(URL url){
		try{
			// Clean the url to make a file name an decrease length if necessary
			String cleanURL = url.toString();
			if(cleanURL.length() > 256){
				cleanURL = cleanURL.substring(0, 256);
			}
		
			int index = cleanURL.hashCode() & (size-1);
			long location = index * (8+256);
		
			URLConnection connection = url.openConnection(); 
				
			cache.seek(location);
			
			byte[] bArray = new byte[256];
			long timeAdded = cache.readLong();
			cache.read(bArray);
			String fileName = new String(bArray);
						
			if(timeAdded == 0 && fileName.trim().isEmpty()){
				cache.seek(location);
				cache.writeLong(connection.getDate());
				cache.write(cleanURL.getBytes());
				return;
			}else if (!fileName.trim().isEmpty()){
				if (!fileName.trim().equals(cleanURL)){
					long initial = location;
					while(!fileName.trim().isEmpty()){
						location = location + 264;
						if(location == initial){
							rehash();
							put(url);
						}else{
							if( location >= cache.length()){
								location = 0;
							}
							cache.seek(location);
							bArray = new byte[256];
							timeAdded = cache.readLong();
							cache.read(bArray);
							fileName = new String(bArray);
						}
					}
					
					cache.seek(location);
					cache.writeLong(connection.getDate());
					cache.write(cleanURL.getBytes());
					return;
					
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void rehash(){
		System.out.println("Rehashing .....");

		try{
			File temp = new File("tempCache");
			RandomAccessFile tempCache = new RandomAccessFile(temp,"rw");
			int prevSize = this.size;
			this.size = prevSize * 2;
			tempCache.setLength(this.size*(8+256));
			for(int i = 0; i< prevSize ; i++){
				// FIND IN THE OLD
				long location = i * (8+256);
				cache.seek(location);
			
				long timeAdded = cache.readLong();
			
				byte[] bArray = new byte[256];
				cache.read(bArray);
				String fileName = new String(bArray);
				
				//PLACE IN NEW
				int index = fileName.trim().hashCode() & (size - 1);
				long newLocation = index * (8+256);
				
				tempCache.seek(newLocation);
			
				long tempTimeAdded = tempCache.readLong();
			
				byte[] tempArray = new byte[256];
				tempCache.read(tempArray);
				String newFileName = new String(tempArray);
				
				if (!newFileName.trim().isEmpty()){
					if (!newFileName.trim().equals(fileName)){
						long initial = newLocation;
						while(!newFileName.trim().isEmpty()){
							newLocation += 264;
							if(newLocation == initial){
								rehash();
								put(new URL(fileName));
							}else{
								if( newLocation >= tempCache.length()){
									newLocation = 0;
								}
								tempCache.seek(newLocation);
								tempArray = new byte[256];
								tempTimeAdded = tempCache.readLong();
								tempCache.read(tempArray);
								newFileName = new String(tempArray);
							}
						}
					}
				}
				
		
				tempCache.seek(newLocation);
				tempCache.writeLong(timeAdded);
				tempCache.write(fileName.trim().getBytes());	
						
			}
		
			cache = tempCache;
			cacheFile.delete();
			temp.renameTo(new File("cache"));
			cacheFile = temp;
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void updateTime(URL url){
		try{
			String cleanURL = url.toString();
		
			if(cleanURL.length() > 256){
				cleanURL = cleanURL.substring(0, 256);
			}
		
			int index = cleanURL.hashCode() & ( size - 1 ) ;
			long location = getLocation(url);
			
			URLConnection connection = url.openConnection(); 
			if(location >= 0){
				cache.seek(location);
				cache.writeLong(connection.getDate());	
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public long needToChange(URL url){
		try{
			String cleanURL = url.toString();
		
			if(cleanURL.length() > 256){
				cleanURL = cleanURL.substring(0, 256);
			}
			
			URLConnection connection = url.openConnection(); 
			
			if(get(url) < connection.getLastModified()){
				System.out.println("Need to Refresh: "+url);
				return getLocation(url);	
			}
			System.out.println("No Refresh Needed for: "+url);
			return -1;
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}
	}
	
	
	public long getLocation(URL url){
		try{
			String cleanURL = url.toString();
			if(cleanURL.length() > 256){
				cleanURL = cleanURL.substring(0, 256);
			}

			int index = cleanURL.hashCode() & ( size - 1 ) ;
			long location = index * (8+256);
					
			URLConnection connection = url.openConnection(); 
		
			cache.seek(location + 8);
			
			byte[] bArray = new byte[256];
			cache.read(bArray);
			String fileName = new String(bArray);
			
			if (!fileName.trim().equals(cleanURL)){
					long initial = location;
					while(!fileName.trim().equals(cleanURL)){
						location += 264 ;
						if(location == initial){
							 return -1;
						}else{
							if( location >= cache.length()){
								location = 0;
							}
							cache.seek(location + 8);
							bArray = new byte[256];
							cache.read(bArray);
							fileName = new String(bArray);
						}
					}	
			}else{
				cache.seek(location + 8);
				
				bArray = new byte[256];
				cache.read(bArray);
				fileName = new String(bArray);
			}
		
			return location;
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}
		
	}
	
	public long get(URL url){
		try{
			String cleanURL = url.toString();
			if(cleanURL.length() > 256){
				cleanURL = cleanURL.substring(0, 256);
			}
			int index = cleanURL.hashCode() & ( size - 1 ) ;
			long location = index * (8+256);
		
			URLConnection connection = url.openConnection(); 
		
			cache.seek(location);
			long timeAdded = cache.readLong();
			byte[] bArray = new byte[256];
			cache.read(bArray);
			String fileName = new String(bArray);
			
			if (!fileName.trim().equals(cleanURL)){
					long initial = location;
					while(!fileName.trim().equals(cleanURL)){
						location += 264 ;
						if(location == initial){
							 return -1;
						}else{
							if( location >= cache.length()){
								location = 0;
							}
							cache.seek(location );
							bArray = new byte[256];
							timeAdded = cache.readLong();
							cache.read(bArray);
							fileName = new String(bArray);
						}
					}	
			}else{
				cache.seek(location);
				timeAdded = cache.readLong();
				bArray = new byte[256];
				cache.read(bArray);
				fileName = new String(bArray);
			}
		
			return timeAdded;
		}catch(IOException e){
			e.printStackTrace();
			return 0;
		}
		
	}
}