package com.avit.safe.sqlite;

import android.util.LruCache;

public class MemoryCache<K,V> extends LruCache<K, V>
{
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }
}
