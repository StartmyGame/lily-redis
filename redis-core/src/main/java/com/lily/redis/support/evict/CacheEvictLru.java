package com.lily.redis.support.evict;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheEntry;
import com.lily.redis.api.ICacheEvictContext;
import com.lily.redis.model.CacheEntry;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * 丢弃策略-LRU
 *
 */

public class CacheEvictLru<K, V> extends AbstractCacheEvict<K, V> {

//    日志相关，后续要研究的
    private static final Log log = LogFactory.getLog(CacheEvictLru.class);

    private final List<K> list = new LinkedList<>();

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context){
        ICacheEntry<K, V> result = null;
        final ICache<K, V> cache = context.cache();

        if(cache.size() >= context.size()){
            K evictKey = list.get(list.size() - 1);
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        return result;
    }

    /**
     *
     * lru策略中更新已存在队列中的数据至最新，或者之间增加数据
     * 目前时间复杂度是O(n)，之后可以优化
     *
     * @param key
     */
    @Override
    public void updateKey(final K key){
        this.list.remove(key);
        this.list.add(0, key);
    }

    @Override
    public void removeKey(final K key){
        this.list.remove(key);
    }
}
