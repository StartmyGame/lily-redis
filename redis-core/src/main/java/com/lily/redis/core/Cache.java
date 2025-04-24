package com.lily.redis.core;

import com.lily.redis.annotation.CacheInterceptor;
import com.lily.redis.api.*;
import com.lily.redis.constant.enums.CacheRemoveType;
import com.lily.redis.exception.CacheRuntimeException;
import com.lily.redis.support.evict.CacheEvictContext;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.lily.redis.support.expire.CacheExpire;
import com.lily.redis.support.listener.remove.CacheRemoveListenerContext;
import com.lily.redis.support.persist.InnerCachePersist;
import com.lily.redis.support.proxy.CacheProxy;

import java.util.*;

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

    /**
     *
     * 过期策略
     *
     */
    private ICacheExpire<K, V> expire;

    /**
     *
     * 删除监听类
     *
     */
    private List<ICacheRemoveListener<K, V>> removeListeners;

    /**
     *
     * 慢日志监听类
     *
     */
    private List<ICacheSlowListener> slowListeners;

    /**
     *
     * 加载类
     *
     */
    private ICacheLoad<K, V> load;

    /**
     *
     * 持久化
     *
     */
    private ICachePersist<K, V> persist;

    public Cache<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    public Cache<K, V> sizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        return this;
    }

    public Cache<K, V> evict(ICacheEvict<K, V> cacheEvict){
        this.evict = cacheEvict;
        return this;
    }

    @Override
    public ICachePersist<K, V> persist(){
        return persist;
    }

    @Override
    public ICacheEvict<K, V> evict(){
        return this.evict;
    }

    public void persist(ICachePersist<K, V> persist){
        this.persist = persist;
    }

    @Override
    public List<ICacheRemoveListener<K, V>> removeListeners() {
        return removeListeners;
    }

    public Cache<K, V> removeListeners(List<ICacheRemoveListener<K, V>> removeListeners) {
        this.removeListeners = removeListeners;
        return this;
    }

    @Override
    public List<ICacheSlowListener> slowListeners() {
        return slowListeners;
    }

    public Cache<K, V> slowListeners(List<ICacheSlowListener> slowListeners) {
        this.slowListeners = slowListeners;
        return this;
    }

    @Override
    public ICacheLoad<K, V> load() {
        return load;
    }

    public Cache<K, V> load(ICacheLoad<K, V> load) {
        this.load = load;
        return this;
    }

    public void init(){
        this.expire = new CacheExpire<>(this);
        this.load.load(this);

        if(this.persist != null){
            new InnerCachePersist<>(this, persist);
        }
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

    @Override
    @CacheInterceptor(evict = true)
    @SuppressWarnings("unchecked")
    public V get(Object key){
        K genericKey = (K) key;
        this.expire.refreshExpire(Collections.singletonList(genericKey));

        return map.get(key);
    }

    private boolean isSizeLimit(){
        final int currentSize = this.size();
        return currentSize >= this.sizeLimit;
    }

    @Override
    @CacheInterceptor(refresh = true, aof = true)
    public void clear() {
        map.clear();
    }

    @Override
    @CacheInterceptor
    public ICache<K, V> expire(K key, long timeInMills){
        long expireTime = System.currentTimeMillis() + timeInMills;

        Cache<K, V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(this);
        return cachePoxy.expireAt(key, expireTime);
    }


    @Override
    @CacheInterceptor(aof = true, evict = true)
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    @CacheInterceptor(aof = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Collection<V> values() {
        return map.values();
    }

    @Override
    @CacheInterceptor(aof = true)
    public ICache<K, V> expireAt(K key, long timeInMills){
        this.expire.expire(key, timeInMills);
        return this;
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    @CacheInterceptor
    public ICacheExpire<K, V> expire() {
        return this.expire;
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
