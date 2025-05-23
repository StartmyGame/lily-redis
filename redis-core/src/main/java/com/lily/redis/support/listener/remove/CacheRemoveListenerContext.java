package com.lily.redis.support.listener.remove;


import com.lily.redis.api.ICacheRemoveListenerContext;

/**
 *
 * 删除的监听器
 *
 */
public class CacheRemoveListenerContext<K, V> implements ICacheRemoveListenerContext<K, V> {

    private K key;

    private V value;

    private String type;

    public static <K, V> CacheRemoveListenerContext<K, V> newInstance(){
        return new CacheRemoveListenerContext<>();
    }

    @Override
    public K key(){
        return key;
    }

    public CacheRemoveListenerContext<K, V> key(K key){
        this.key = key;
        return this;
    }

    @Override
    public  V value(){
        return value;
    }

    public CacheRemoveListenerContext<K, V> value(V value){
        this.value = value;
        return this;
    }

    @Override
    public String type(){
        return type;
    }

    public CacheRemoveListenerContext<K, V> type(String type){
        this.type = type;
        return this;
    }
}
