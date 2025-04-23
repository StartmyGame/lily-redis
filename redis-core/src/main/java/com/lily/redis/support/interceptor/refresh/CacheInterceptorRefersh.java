package com.lily.redis.support.interceptor.refresh;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheInterceptor;
import com.lily.redis.api.ICacheInterceptorContext;

/**
 *
 * 刷新
 *
 * @param <K>
 * @param <V>
 */
public class CacheInterceptorRefersh<K, V> implements ICacheInterceptor<K, V> {

    private static final Log log = LogFactory.getLog(CacheInterceptorRefersh.class);

    public void before(ICacheInterceptorContext<K, V> context){
        log.debug("Refresh start");

        final ICache<K, V> cache = context.cache();
        cache.expire().refreshExpire(cache.keySet());
    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {}
}
