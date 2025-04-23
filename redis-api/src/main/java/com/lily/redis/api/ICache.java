package com.lily.redis.api;

import java.util.List;
import java.util.Map;

public interface ICache<K, V> extends Map<K, V> {


    /**
     *
     * 删除监听类列表
     *
     * @return
     */
    List<ICacheRemoveListener<K, V>> removeListeners();


    /**
     *
     * 设置过期时间
     * （1）如果key不存在，则什么都不做
     *
     *
     * 惰性删除
     * 定点删除
     *
     *
     * @param key
     * @param timeInMills
     * @return
     */
    ICache<K, V> expire(final K key, final long timeInMills);


    /**
     *
     * 在指定的时间过期
     *
     * @param key
     * @param timeInMills
     * @return
     */
    ICache<K, V> expireAt(final K key, final long timeInMills);

    /**
     *
     * 获取缓存的过期处理类
     *
     * @return
     */
    ICacheExpire<K, V> expire();

    List<ICacheSlowListener> slowListeners();

}
