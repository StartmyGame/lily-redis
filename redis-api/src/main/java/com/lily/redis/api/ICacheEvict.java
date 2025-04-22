package com.lily.redis.api;


/**
 *
 * 驱逐策略
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheEvict<K, V> {

    /**
     *
     * 驱逐策略
     *
     * @param context
     * @return
     */
    ICacheEntry<K, V> evict(final ICacheEvictContext<K, V> context);

    /**
     *
     * 更新key信息
     *
     * @param key
     */
    void updateKey(final K key);


    /**
     *
     * 删除key信息
     *
     * @param key
     */
    void removeKey(final K key);

}
