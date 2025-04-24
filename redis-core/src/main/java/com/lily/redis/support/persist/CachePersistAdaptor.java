package com.lily.redis.support.persist;

import com.lily.redis.api.ICache;
import com.lily.redis.api.ICachePersist;

import java.util.concurrent.TimeUnit;

/**
 *
 * 缓存持久化-适配器模式
 *
 * @param <K>
 * @param <V>
 */
public class CachePersistAdaptor<K, V> implements ICachePersist<K, V> {

    /**
     *
     * 持久化
     * key长度 key+value
     *
     *
     * @param cache
     */
    @Override
    public void persist(ICache<K, V> cache){}

    @Override
    public long delay(){
        return this.period();
    }

    @Override
    public long period(){
        return 1;
    }

    @Override
    public TimeUnit timeUnit(){
        return TimeUnit.SECONDS;
    }
}
