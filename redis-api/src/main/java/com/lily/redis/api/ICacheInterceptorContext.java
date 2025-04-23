package com.lily.redis.api;


import java.lang.reflect.Method;

/**
 *
 * 拦截器上下文接口
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheInterceptorContext<K, V> {

    /**
     *
     * 缓存信息
     *
     * @return
     */
    ICache<K, V> cache();

    /**
     *
     * 执行的方法信息
     *
     * @return
     */
    Method method();

    /**
     *
     * 执行的参数
     *
     * @return
     */
    Object[] params();

    /**
     *
     * 方法执行的结果
     *
     * @return
     */
    Object result();

    /**
     *
     * 开始时间
     *
     * @return
     */
    long startMills();

    /**
     *
     * 结束时间
     *
     * @return
     */
    long endMills();

}
