package com.lily.redis.support.proxy.cglib;

import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheProxy;
import com.lily.redis.api.ICacheProxyBsContext;
import com.lily.redis.support.proxy.bs.CacheProxyBs;
import com.lily.redis.support.proxy.bs.CacheProxyBsContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor, ICacheProxy {

    /**
     *
     * 被代理的对象
     *
     */
    private final ICache target;

    public CglibProxy(ICache target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        ICacheProxyBsContext context = CacheProxyBsContext.newInstance()
                .method(method).params(params).target(target);

        return CacheProxyBs.newInstance().context(context).exceute();
    }

    @Override
    public Object proxy(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }
}
