package com.lily.redis.api;

import java.util.concurrent.TimeUnit;

/**
 *
 * 持久化缓存接口
 *
 *
 * @param <K>
 * @param <V>
 */
public interface ICachePersist<K, V> {

    /**
     *
     * 持久化缓存信息
     *
     * @param cache
     */
    void persist(final ICache<K, V> cache);


    /**
     *
     * 延迟时间
     *
     * @return
     */
    long delay();

    /**
     *
     * 时间间隔
     *
     * @return
     */
    long period();

    /**
     *
     * 时间单位
     *
     * @return
     */
    TimeUnit timeUnit();
}
