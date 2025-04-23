package com.lily.redis.support.proxy.bs;


import com.lily.redis.api.ICacheInterceptor;
import com.lily.redis.api.ICacheProxyBsContext;

import java.util.List;

/**
 *
 * 代理指导类
 *
 */
public final class CacheProxyBs {

    private CacheProxyBs() {}

    /**
     *
     * 代理上下文
     *
     */
    private ICacheProxyBsContext context;

    private final List<ICacheInterceptor> commonInterceptors =

}
