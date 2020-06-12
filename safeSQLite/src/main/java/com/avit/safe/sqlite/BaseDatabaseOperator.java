package com.avit.safe.sqlite;

import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseDatabaseOperator<KEY, DATA> implements IDatabaseOperation<DATA> {

    private final Cache<KEY, DATA> memoryCache = new Cache<>(this);

    protected KEY dataKey(DATA data){
        throw new IllegalStateException("not implement");
    }

    public Cache<KEY, DATA> getMemoryCache() {
        return memoryCache;
    }

    @Override
    public synchronized int saveOrUpdate(DATA data) {
        getMemoryCache().putCache(data);
        return 0;
    }

    @Override
    public synchronized int saveOrUpdate(List<DATA> datas) {
        getMemoryCache().putCache(datas);
        return 0;
    }

    @Override
    public DATA read(DATA data) {
        return getMemoryCache().getCache(dataKey(data));
    }

    @Override
    public List<DATA> read(Map<String, Object> objects) {
        Log.w(getLogTag(), "dummy read: " + objects);
        return Collections.emptyList();
    }

    @Override
    public List<DATA> read(int pageSize, int offset, Map<String, Object> object) {
        Log.w(getLogTag(), "dummy read: pageSize = " + pageSize + ", offset = " + offset + ", " + object);
        return Collections.emptyList();
    }

    @Override
    public List<DATA> read() {
        Log.w(getLogTag(), "dummy read: all");
        return Collections.emptyList();
    }

    @Override
    public synchronized int delete(DATA data) {
        getMemoryCache().removeCache(dataKey(data));
        return 0;
    }

    @Override
    public synchronized int delete(Map<String, Object> objects) {
        Log.w(getLogTag(), "dummy delete: " + objects);
        return 0;
    }

    @Override
    public synchronized int delete(List<DATA> datas) {
        getMemoryCache().removeCache(datas);
        return 0;
    }

    @Override
    public synchronized int delete() {
        getMemoryCache().removeCache();
        return 0;
    }

    @Override
    public int count() {
        Log.w(getLogTag(), "dummy count: all");
        return 0;
    }

    @Override
    public int count(DATA data) {
        Log.w(getLogTag(), "dummy count: " + data);
        return 0;
    }

    @Override
    public int count(Map<String, Object> objects) {
        Log.w(getLogTag(), "dummy count: " + objects);
        return 0;
    }

    public final static class Cache<KEY, DATA>{
        private final BaseDatabaseOperator<KEY, DATA> databaseOperator;
        private final MemoryCache<KEY, DATA> memoryCache = new MemoryCache<>(5 * 1024 * 1024);
        private volatile boolean isEnableMemoryCache;

        public Cache(BaseDatabaseOperator<KEY, DATA> operator) {
            this.databaseOperator = operator;
        }

        public final void  enableMemoryCache(){
            isEnableMemoryCache = true;
        }

        public final void  disableMemoryCache(){
            isEnableMemoryCache = false;
        }

        public KEY dataKey(DATA data){
            return databaseOperator.dataKey(data);
        }

        public final DATA getCache(KEY key){
            if (isEnableMemoryCache){
                return memoryCache.get(key);
            }
            return null;
        }

        public final void putCache(DATA data){
            if (isEnableMemoryCache) {
                memoryCache.put(dataKey(data), data);
            }
        }

        public final void putCache(List<DATA> datas){
            if (isEnableMemoryCache) {
                for (DATA data : datas) {
                    memoryCache.put(dataKey(data), data);
                }
            }
        }

        public final void removeCache(KEY key){
            if (isEnableMemoryCache){
                memoryCache.remove(key);
            }
        }

        public final void removeCache(List<DATA> datas) {
            if (isEnableMemoryCache) {
                for (DATA data : datas) {
                    memoryCache.remove(dataKey(data));
                }
            }
        }
        public final void removeCache() {
            if (isEnableMemoryCache) {
                memoryCache.evictAll();
            }
        }
    }
}
