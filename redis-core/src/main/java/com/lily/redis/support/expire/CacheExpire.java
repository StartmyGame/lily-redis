package com.lily.redis.support.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.heaven.util.util.MapUtil;
import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheExpire;
import com.lily.redis.api.ICacheRemoveListener;
import com.lily.redis.api.ICacheRemoveListenerContext;
import com.lily.redis.constant.enums.CacheRemoveType;
import com.lily.redis.support.listener.remove.CacheRemoveListenerContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * 缓存过期-普通策略
 *
 * @param <K>
 * @param <V>
 */
public class CacheExpire<K, V> implements ICacheExpire<K, V> {

    /**
     *
     * 单次清空的数量限制
     *
     */
    private static final int LIMIT = 100;

    private final Map<K, Long> expireMap = new HashMap<>();

    /**
     *
     * 缓存实现
     *
     */
    private final ICache<K, V> cache;

    public CacheExpire(final ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 100, 100, TimeUnit.MILLISECONDS);
    }

    private class ExpireThread implements Runnable {
        @Override
        public void run() {
            if(MapUtil.isEmpty(expireMap)){
                return;
            }

            int count = 0;
            for(Map.Entry<K, Long> entry : expireMap.entrySet()){
                if(count >= LIMIT){
                    return;
                }

                expireKey(entry.getKey(), entry.getValue());
                count++;
            }
        }
    }

    @Override
    public void expire(K key, long expireAt){
        expireMap.put(key, expireAt);
    }

    public void refreshExpire(Collection<K> keyList){
        if(CollectionUtil.isEmpty(keyList)){
            return;
        }

        if(keyList.size() <= expireMap.size()){
            for(K key : keyList){
                Long expireAt = expireMap.get(key);
                expireKey(key, expireAt);
            }
        }
        else{
            for (Map.Entry<K, Long> entry : expireMap.entrySet()){
                this.expireKey(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

    private void expireKey(final K key, final Long expireAt){
        if(expireAt == null){
            return;
        }

        long currentTime = System.currentTimeMillis();
        if(currentTime - expireAt >= 0){
            expireMap.remove(key);
//            再移除缓存，后续可以通过惰性删除做补偿
            V removeValue = cache.remove(key);

            ICacheRemoveListenerContext<K, V> removeListenerContext = CacheRemoveListenerContext.<K, V>newInstance()
                    .key(key)
                    .value(removeValue)
                    .type(CacheRemoveType.EXPIRE.code());

            for (ICacheRemoveListener<K, V> listener : cache.removeListeners()){
                listener.listen(removeListenerContext);
            }
        }
    }
}
