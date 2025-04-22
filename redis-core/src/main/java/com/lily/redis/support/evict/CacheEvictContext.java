package com.lily.redis.support.evict;

import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheEvictContext;


/**
 *
 * 驱逐策略
 * 新增的key
 * 实现cache
 * 淘汰监听器
 *
 * @param <K>
 * @param <V>
 */
public class CacheEvictContext<K, V> implements ICacheEvictContext<K, V> {


    private K key;

    private ICache<K, V> cache;

    private int size;


    @Override
    public K key(){
        return key;
    }

    public CacheEvictContext<K, V> key(K key){
        this.key = key;
        return this;
    }

    @Override
    public ICache<K, V> cache(){
        return cache;
    }

    public CacheEvictContext<K, V> cache(ICache<K, V> cache){
        this.cache = cache;
        return this;
    }

    @Override
    public int size(){return size;}

    public CacheEvictContext<K, V> size(int size){
        this.size = size;
        return this;
    }

}
