package com.lily.redis.api;

/**
 *
 * 缓存接口
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheLoad<K, V> {

    /**
     *
     * 加载缓存信息
     *
     * @param cache
     */
    void load(final ICache<K, V> cache);
}
