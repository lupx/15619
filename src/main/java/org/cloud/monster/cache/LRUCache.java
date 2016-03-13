package org.cloud.monster.cache;

import java.util.LinkedHashMap;

/**
 * The self-implemented LRU cache
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxCapacity;

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    public LRUCache(int maxCapacity) {
        super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
        this.maxCapacity = maxCapacity;
    }

    /**
     * Removes LRU element from the cache when size reaches Capacity which is 999.
     */
    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }


    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }


    @Override
    public V get(Object key) {
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        return super.put(key, value);
    }
}
