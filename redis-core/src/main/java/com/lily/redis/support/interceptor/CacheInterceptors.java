package com.lily.redis.support.interceptor;

import com.lily.redis.api.ICacheInterceptor;
import com.lily.redis.support.interceptor.common.CacheInterceptorCost;
import com.lily.redis.support.interceptor.refresh.CacheInterceptorRefersh;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 缓存拦截器工具类
 *
 */
public final class CacheInterceptors {


    /**
     *
     * 默认通用
     *
     * @return
     */
    @SuppressWarnings("all")
    public static List<ICacheInterceptor> defaultCommonList(){
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorCost());
        return list;
    }


    /**
     *
     * 默认刷新
     *
     * @return
     */
    public static List<ICacheInterceptor> defalutRefreshList(){
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorRefersh());
        return list;
    }

}
