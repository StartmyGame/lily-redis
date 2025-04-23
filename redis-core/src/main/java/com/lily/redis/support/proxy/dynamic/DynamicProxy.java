package com.lily.redis.support.proxy.dynamic;

import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheProxy;
import com.lily.redis.api.ICacheProxyBsContext;
import com.lily.redis.support.proxy.bs.CacheProxyBsContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * 动态代理
 *
 * ？？
 *
 */
public class DynamicProxy implements InvocationHandler, ICacheProxy {

    private final ICache target;

    public DynamicProxy(ICache target){
        this.target = target;
    }

    @Override
    @SuppressWarnings("all")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ICacheProxyBsContext context = CacheProxyBsContext.newInstance()
                .method(method).params(args).target(target);

    }
}
