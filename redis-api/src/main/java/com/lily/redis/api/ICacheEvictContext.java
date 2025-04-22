package com.lily.redis.api;

public interface ICacheEvictContext<K, V> {

    /**
     * 获取key值
     * @return
     */
    K key();


    ICache<K, V> cache();

    /**
     * 获取大小
     */
    int size();
}
