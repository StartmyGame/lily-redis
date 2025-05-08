package com.lily.redis.support.evict;


import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheEntry;
import com.lily.redis.api.ICacheEvictContext;
import com.lily.redis.exception.CacheRuntimeException;
import com.lily.redis.model.CacheEntry;
import com.lily.redis.model.DoubleListNode;

import java.util.HashMap;
import java.util.Map;

/**
 * 用map进行优化LRU
 *
 *
 */
public class CacheEvictLru2<K, V> extends AbstractCacheEvict<K, V> {

    private static final Log log = LogFactory.getLog(CacheEvictLru2.class);

    private DoubleListNode<K, V> head;
    private DoubleListNode<K, V> tail;

    private Map<K, DoubleListNode<K, V>> indexMap;

    public CacheEvictLru2(){
        this.indexMap = new HashMap<>();
        this.head = new DoubleListNode<>();
        this.tail = new DoubleListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context){
        ICacheEntry<K, V> result = null;
        final ICache<K, V> cache = context.cache();

        if(cache.size() >= context.size()){
            DoubleListNode<K, V> tailPre = this.tail.pre();
            if(tailPre == this.head){
                log.error("当前列表为空，无法进行删除");
                throw new CacheRuntimeException("不可删除头节点");
            }

            K evictKey = tailPre.key();
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        return result;
    }

    @Override
    public void updateKey(final K key){
        this.removeKey(key);

        DoubleListNode<K, V> newNode = new DoubleListNode<>();
        newNode.key(key);

        DoubleListNode<K, V> next = this.head.next();

        newNode.pre(this.head);
        newNode.next(next);

        this.head.next(newNode);

        next.pre(newNode);

        indexMap.put(key, newNode);
    }

    @Override
    public void removeKey(final K key){
        DoubleListNode<K, V> node = indexMap.get(key);

        if(ObjectUtil.isNull(node)){
            return;
        }

        DoubleListNode<K, V> pre = node.pre();
        DoubleListNode<K, V> next = node.next();

        pre.next(next);
        next.pre(pre);

        this.indexMap.remove(key);
    }

}
