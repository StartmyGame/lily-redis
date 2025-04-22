package com.lily.redis.core;

import com.lily.redis.annotation.CacheInterceptor;
import com.lily.redis.api.*;
import com.lily.redis.constant.enums.CacheRemoveType;
import com.lily.redis.exception.CacheRuntimeException;
import com.lily.redis.support.evict.CacheEvictContext;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.lily.redis.support.listener.remove.CacheRemoveListenerContext;

import java.util.List;
import java.util.Map;

public class Cache<K, V> implements ICache<K, V>{


    /**
     *
     * map信息
     *
     */
    private Map<K, V> map;

    private int sizeLimit;

    /**
     *
     * 驱逐策略
     *
     */
    private ICacheEvict<K, V> evict;

    private List<ICacheRemoveListener<K, V>> removeListeners;

    @Override
    public List<ICacheRemoveListener<K, V>> removeListeners() {
        return removeListeners;
    }

    public Cache<K, V> removeListeners(List<ICacheRemoveListener<K, V>> removeListeners) {
        this.removeListeners = removeListeners;
        return this;
    }

    @Override
    public V put(K key, V value) {
        CacheEvictContext<K, V> context = new CacheEvictContext<>();
        context.key(key).size(sizeLimit).cache(this);

        ICacheEntry<K, V> evictEntry = evict.evict(context);

        if(ObjectUtil.isNotNull(evictEntry)){
            ICacheRemoveListenerContext<K, V> removeListenerContext = CacheRemoveListenerContext.<K, V>newInstance().key(evictEntry.key())
                    .value(evictEntry.value())
                    .type(CacheRemoveType.EVICT.code());

            for(ICacheRemoveListener<K, V> listener : context.cache().removeListeners()){
                listener.listen(removeListenerContext);
            }
        }

        if(isSizeLimit()){
            throw new CacheRuntimeException("当前队列已满，数据添加失败！");
        }

        return map.put(key, value);

    }

    private boolean isSizeLimit(){
        final int currentSize = this.size();
        return currentSize >= this.sizeLimit;
    }



    @Override
    @CacheInterceptor(refresh = true)
    public int size() {
        return map.size();
    }


    @Override
    @CacheInterceptor(refresh = true)
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    @CacheInterceptor(refresh = true, evict = true)
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }
}
