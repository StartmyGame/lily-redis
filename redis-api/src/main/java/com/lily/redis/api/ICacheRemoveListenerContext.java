package com.lily.redis.api;


/**
 *
 * 删除监听器上下文
 *
 * 耗时统计
 * 监听器
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheRemoveListenerContext<K, V> {

    /**
     * 被删的key
     * @return
     */
    K key();

    V value();

    String type();
}
