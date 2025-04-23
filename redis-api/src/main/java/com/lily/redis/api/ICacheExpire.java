package com.lily.redis.api;

import java.util.Collection;

/**
 *
 * 缓存过期接口
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheExpire<K, V> {

    /**
     *
     * 指定过期信息
     *
     * @param key
     * @param expireAt
     */
    void expire(final K key, final long expireAt);


    /**
     *
     * 惰性删除中需要处理的keys
     *
     * @param keyList
     */
    void refreshExpire(final Collection<K> keyList);

    /**
     *
     * 待过期的key
     * 不存在则是返回null
     *
     * @param key
     * @return
     */
    Long expireTime(final K key);
}
