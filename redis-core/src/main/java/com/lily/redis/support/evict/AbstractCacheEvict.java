package com.lily.redis.support.evict;


import com.lily.redis.api.ICacheEntry;
import com.lily.redis.api.ICacheEvict;
import com.lily.redis.api.ICacheEvictContext;

/**
 *
 * 丢弃策略-抽象实现类
 *
 */
public abstract class AbstractCacheEvict<K, V> implements ICacheEvict<K, V> {

    @Override
    public ICacheEntry evict(ICacheEvictContext<K, V> context){
        return doEvict(context);
    }


    /**
     *
     * 开始移除
     *
     * @param context
     * @return
     */
    protected abstract ICacheEntry doEvict(ICacheEvictContext<K, V> context);

    @Override
    public void updateKey(K key){}

    @Override
    public void removeKey(K key){}
}
