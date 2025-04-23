package com.lily.redis.support.proxy.none;


import com.lily.redis.api.ICacheProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * 没有代理
 *
 */
public class NoneProxy implements InvocationHandler, ICacheProxy {

    /**
     *
     * 代理对象
     *
     */
    private final Object target;

    public NoneProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target, args);
    }

    @Override
    public Object proxy(){
        return this.target;
    }

}
