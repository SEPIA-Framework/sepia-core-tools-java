package net.b07z.sepia.server.core.java;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Map implementation that uses {@link LinkedHashMap} to features to keep the size limited to a maximum and drop the oldest entry on overflow.<br>
 * NOTE: Check the constructor on notes about concurrency.  
 * 
 * @author Florian Quirin
 *
 * @param <K>
 * @param <V>
 */
public class MaxSizeMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	
	private int maxKeysSize;
	
	/**
	 * Create a {@link LinkedHashMap} that automatically removes the oldest entry when size is exceeded.<br>
	 * <br>
	 * IMPORTANT: This map (as is it basically a linked hash map) can run into concurrency problems, use {@link #getSynchronizedMap(int)}
	 * or build your own Collections.synchronizedMap(...) if you access the list from multiple threads.
	 * @param maxSize
	 */
	public MaxSizeMap(int maxSize){
		super(maxSize + 1);
		this.maxKeysSize = maxSize;
	}
	/**
	 * Create a synchronized version of this map.
	 * @param maxSize
	 * @return
	 */
	public static <T> Map<String, T> getSynchronizedMap(int maxSize){
		return Collections.synchronizedMap(new MaxSizeMap<String, T>(maxSize));
	}
	
	@Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return size() > maxKeysSize;
    }

}
