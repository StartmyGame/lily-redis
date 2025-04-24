package com.lily.redis.support.interceptor.aof;

import com.alibaba.fastjson.JSON;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheInterceptor;
import com.lily.redis.api.ICacheInterceptorContext;
import com.lily.redis.api.ICachePersist;
import com.lily.redis.model.PersistAofEntry;
import com.lily.redis.support.persist.CachePersistAof;

public class CacheInterceptorAof<K, V> implements ICacheInterceptor<K, V> {

    private static final Log log = LogFactory.getLog(CacheInterceptorAof.class);

    @Override
    public void before(ICacheInterceptorContext<K, V> context){}

    @Override
    public void after(ICacheInterceptorContext<K, V> context){
        ICache<K, V> cache = context.cache();
        ICachePersist<K, V> persist = cache.persist();

        if(persist instanceof CachePersistAof){
            CachePersistAof<K, V> cachePersistAof = (CachePersistAof<K, V>)persist;

            String methodName = context.method().getName();
            PersistAofEntry aofEntry = PersistAofEntry.newInstance();
            aofEntry.setMethodName(methodName);
            aofEntry.setParams(context.params());

            String json = JSON.toJSONString(aofEntry);

            log.debug("AOF 开始追加文件内容：{}", json);
            cachePersistAof.append(json);
            log.debug("AOF 完成追加文件内容：{}",json);

        }
    }


}
