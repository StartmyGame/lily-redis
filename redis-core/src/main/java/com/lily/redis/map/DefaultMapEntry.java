package com.lily.redis.map;

import java.util.Map;

public class DefaultMapEntry<K, V> implements Map.Entry<K, V> {

    private K key;

    private V value;

    public DefaultMapEntry() {

    }

    public DefaultMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    public void setKey(K key){
        this.key = key;
    }

    @Override
    public V getValue(){
        return value;
    }

    @Override
    public V setValue(V value){
        this.value = value;
        return this.value;
    }

    @Override
    public String toString(){
        return "{" + key + "=" + value + "}";
    }

}
