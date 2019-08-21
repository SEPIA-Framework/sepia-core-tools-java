package net.b07z.sepia.server.core.tools;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LruCache<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = -1541387922963793050L;
	
	private int cacheSize;

	/**
	 * Create a (non-concurrent) Last Recently Used (LRU) cache map.
	 * @param cacheSize - max entries in cache
	 * @param accessOrder - the ordering mode: true for access-order, false for insertion-order
	 */
	public LruCache(int cacheSize, boolean accessOrder){
		super(16, 0.75f, accessOrder);
		this.cacheSize = cacheSize;
	}
	
	/**
	 * NOTE: NOT TESTED
	 * @return a synchronized LruCache Map with String - List key-value pairs.
	 */
	public static LruCache<String, List<?>> createSynchronizedLruCache(int cacheSize, boolean accessOrder){
		return (LruCache<String, List<?>>) Collections.synchronizedMap(new LruCache<String, List<?>>(cacheSize, accessOrder));
	}
	
	public void setCacheSize(int newCacheSize){
		this.cacheSize = newCacheSize;
	}
	public int getCacheSize(){
		return this.cacheSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest){
		return size() >= cacheSize;
	}
}
