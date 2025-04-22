package com.lily.redis.api;


/**
 *
 * 删除监听器
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheRemoveListener<K, V> {

    /**
     *
     * 监听
     *
     * @param context
     */
    void listen(final ICacheRemoveListenerContext<K, V> context);
}
