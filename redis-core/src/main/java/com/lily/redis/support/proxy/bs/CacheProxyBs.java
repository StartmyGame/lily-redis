package com.lily.redis.support.proxy.bs;


import com.lily.redis.annotation.CacheInterceptor;
import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheInterceptor;
import com.lily.redis.api.ICachePersist;
import com.lily.redis.api.ICacheProxyBsContext;
import com.lily.redis.support.interceptor.CacheInterceptorContext;
import com.lily.redis.support.interceptor.CacheInterceptors;
import com.lily.redis.support.persist.CachePersistAof;

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

    /**
     *
     * 默认通用拦截器
     *
     */
    @SuppressWarnings("all")
    private final List<ICacheInterceptor> commonInterceptors = CacheInterceptors.defaultCommonList();

    /**
     *
     * 默认刷新拦截器
     *
     */
    @SuppressWarnings("all")
    private final List<ICacheInterceptor> refreashInterceptors = CacheInterceptors.defalutRefreshList();

    /**
     *
     * 持久化拦截器
     *
     */
    @SuppressWarnings("all")
    private final ICacheInterceptor persistInterceptors = CacheInterceptors.aof();

    /**
     *
     * 驱逐拦截器
     *
     */
    @SuppressWarnings("all")
    private final ICacheInterceptor evictInterceptors = CacheInterceptors.evict();

    /**
     * 新建对象实例
     * @return 实例
     * @since 0.0.4
     */
    public static CacheProxyBs newInstance() {
        return new CacheProxyBs();
    }

    public CacheProxyBs context(ICacheProxyBsContext context) {
        this.context = context;
        return this;
    }


    /**
     *
     * 执行
     *
     * @return
     * @throws Throwable
     */
    @SuppressWarnings("all")
    public Object exceute() throws Throwable{
        final long startMills = System.currentTimeMillis();
        final ICache cache = context.target();
        CacheInterceptorContext interceptorContext = CacheInterceptorContext.newInstance()
                .startMills(startMills)
                .method(context.method())
                .params(context.params())
                .cache(context.target());

        CacheInterceptor cacheInterceptor = context.interceptor();
        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, true);

        Object result = context.process();

        final long endMills = System.currentTimeMillis();
        interceptorContext.endMills(endMills).result(result);

        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, false);
        return result;
    }

    /**
     *
     * 拦截器执行类
     *
     * @param cacheInterceptor
     * @param interceptorContext
     * @param cache
     * @param before
     */
    private void interceptorHandler(CacheInterceptor cacheInterceptor,
                                    CacheInterceptorContext interceptorContext,
                                    ICache cache,
                                    boolean before) {

        if(cacheInterceptor == null) return;
//        通用
        if(cacheInterceptor.common()){
            for(ICacheInterceptor interceptor : commonInterceptors) {
                if(before){
                    interceptor.before(interceptorContext);
                }
                else{
                    interceptor.after(interceptorContext);
                }
            }
        }

//        刷新
        if(cacheInterceptor.refresh()){
            for(ICacheInterceptor interceptor : refreashInterceptors) {
                if(before){
                    interceptor.before(interceptorContext);
                }
                else{
                    interceptor.after(interceptorContext);
                }
            }
        }

//        AOF追加
        final ICachePersist cachePersist = cache.persist();
        if(cacheInterceptor.aof() && (cachePersist instanceof CachePersistAof)){
            if(before){
                persistInterceptors.before(interceptorContext);
            }
            else{
                persistInterceptors.after(interceptorContext);
            }
        }

//        驱逐策略更新
        if(cacheInterceptor.evict()){
            if(before){
                evictInterceptors.before(interceptorContext);
            }
            else{
                evictInterceptors.after(interceptorContext);
            }
        }
    }
}
