package com.lily.redis.api;


/**
 *
 *
 * 拦截器接口
 *
 * （1）耗时统计
 * （2）监听器
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheInterceptor<K, V>{

    /**
     *
     * 方法执行之前
     *
     * @param context
     */
    void before(ICacheInterceptorContext<K, V> context);

    /**
     *
     * 方法执行之后
     *
     * @param context
     */
    void after(ICacheInterceptorContext<K, V> context);
}
