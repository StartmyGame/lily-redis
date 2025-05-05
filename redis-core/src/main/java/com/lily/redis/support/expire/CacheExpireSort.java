package com.lily.redis.support.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.lily.redis.api.ICache;
import com.lily.redis.api.ICacheExpire;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.houbb.heaven.util.util.MapUtil;

/**
 *
 * 缓存过期-sort排序
 *
 * @param <K>
 * @param <V>
 */

public class CacheExpireSort<K, V> implements ICacheExpire<K, V> {

    private static final int LIMIT = 100;

    private final Map<Long, List<K>> sortMap = new TreeMap<>(new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            return (int) (o1 - o2);
        }
    });

    private final Map<K, Long> expireMap = new HashMap<>();

    private final ICache<K, V> cache;

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CacheExpireSort(ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 100, 100, TimeUnit.MILLISECONDS);
    }

    private class ExpireThread implements Runnable {
        @Override
        public void run(){
            if(MapUtil.isEmpty(sortMap)){
                return;
            }

            int count = 0;
            for(Map.Entry<Long, List<K>> entry : sortMap.entrySet()){
                final Long expireAt = entry.getKey();
                List<K> expireKeys = entry.getValue();

                if(CollectionUtil.isEmpty(expireKeys)){
                    sortMap.remove(expireAt);
                    continue;
                }
                if(count >= LIMIT){
                    return;
                }

                long currentTime = System.currentTimeMillis();
                if(currentTime >= expireAt){
                    Iterator<K> iterator = expireKeys.iterator();
                    while(iterator.hasNext()){
                        K key = iterator.next();
                        iterator.remove();
                        expireMap.remove(key);
                        cache.remove(key);

                        count++;
                    }
                }
                else{
                    return;
                }
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        List<K> keys = sortMap.get(expireAt);
        if (keys == null){
            keys = new ArrayList<>();
        }
        keys.add(key);

        sortMap.put(expireAt, keys);
        expireMap.put(key, expireAt);
    }

    public void refreshExpire(Collection<K> keyList){
        if(CollectionUtil.isEmpty(keyList)){
            return;
        }

        final int expireSize = expireMap.size();
        if(expireSize <= keyList.size()) {
            // 一般过期的数量都是较少的
            for(Map.Entry<K,Long> entry : expireMap.entrySet()) {
                K key = entry.getKey();

                // 这里直接执行过期处理，不再判断是否存在于集合中。
                // 因为基于集合的判断，时间复杂度为 O(n)
                this.removeExpireKey(key);
            }
        } else {
            for(K key : keyList) {
                this.removeExpireKey(key);
            }
        }
    }

    private void removeExpireKey(K key){
        Long expireTime = expireMap.get(key);
        if(expireTime != null){
            final long currentTime = System.currentTimeMillis();
            if(currentTime >= expireTime){
                expireMap.remove(key);

                List<K> expireKeys = sortMap.get(expireTime);
                expireKeys.remove(key);
                sortMap.put(expireTime, expireKeys);
            }
        }
    }

    @Override
    public Long expireTime(K key){
        return expireMap.get(key);
    }
}
