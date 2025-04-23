package com.lily.redis.api;

import com.lily.redis.annotation.CacheInterceptor;

import java.lang.reflect.Method;

public interface ICacheProxyBsContext {

    /**
     *
     * 拦截器信息
     *
     */
    CacheInterceptor interceptor();

    /**
     *
     * 获取代理对象信息
     *
     * @return
     */
    ICache target();

    /**
     *
     * 目标对象
     *
     * @param target
     * @return
     */
    ICacheProxyBsContext target(final ICache target);


    /**
     *
     * 参数信息
     *
     * @return
     */
    Object[] params();

    /**
     *
     * 方法信息
     *
     * @return
     */
    Method method();

    /**
     *
     * 方法执行
     *
     * @return
     * @throws Throwable
     */
    Object process() throws Throwable;
}
