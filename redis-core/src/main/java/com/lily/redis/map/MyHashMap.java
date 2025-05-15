package com.lily.redis.map;

import com.github.houbb.heaven.support.tuple.impl.Pair;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

import java.util.*;

public class MyHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

    private static final Log log = LogFactory.getLog(MyHashMap.class);

    /**
     *
     * rehash下标
     * 如果rehashIndex != -1 则表明正在进行rehash
     *
     */
    private int rehashIndex = -1;

    /**
     *
     * 容量
     *
     */
    private int capacity;

    /**
     *
     * 处于rehash状态的容量
     *
     */
    private int rehashCapacity;

    /**
     *
     * 尺寸大小
     *
     */
    private int size = 0;

    /**
     *
     * 负载因子
     *
     */
    private final double factor = 0.75;


    /**
     *
     * 每个桶里面是一个列表
     *
     */
    private List<List<Entry<K, V>>> table;

    /**
     *
     * 渐进式rehash时，用来存储元素使用
     *
     */
    private List<List<Entry<K, V>>> rehashTable;

    /**
     *
     * 是否开启debug模式
     *
     */
    private boolean debugMode = false;

    public MyHashMap() {
        this(8);
    }

    public MyHashMap(int capacity) {
        this(capacity, false);
    }

    /**
     *
     * 初始化hashmap
     *
     * @param capacity
     * @param debugMode
     */
    public MyHashMap(int capacity, boolean debugMode) {
        this.capacity = capacity;
        this.table = new ArrayList<>(capacity);

        for(int i = 0; i < capacity; i++){
            this.table.add(new ArrayList<Entry<K, V>>());
        }

        this.debugMode = debugMode;
        this.rehashIndex = -1;
        this.rehashCapacity = -1;
        this.rehashTable = null;
    }

    /**
     *
     * 查询方法
     *
     * @param key the key whose associated value is to be returned
     * @return
     */
    @Override
    public V get(Object key){
        if(isInReHash()){
            if(debugMode){
                log.debug("当前处于rehash状态， 额外执行一次操作");
                rehashToNew();
            }
        }

        V result = getValue(key, this.table);
        if(result != null){
            return result;
        }

        if(isInReHash()){
            return getValue(key, this.rehashTable);
        }

        return null;
    }

    private V getValue(final Object key,
                       final List<List<Entry<K, V>>> table){
        if(ObjectUtil.isNull(table)){
            return null;
        }

        for(List<Entry<K, V>> list : table){
            for(Entry<K, V> entry : list){
                K entryKey = entry.getKey();
                if(ObjectUtil.isNull(key, entryKey) || key.equals(entryKey)){
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public V put(K key, V value){
        boolean isInReHash = isInReHash();
        if(!isInReHash){
            Pair<Boolean, V> pair = updateTableInfo(key, value, this.table, this.capacity);
            if(pair.getValueOne()){
                V oldValue = pair.getValueTwo();

                if(debugMode){
                    log.debug("不处于渐进式rehash，此次为更新操作");
                    printTable(this.table);
                }

                return oldValue;
            }
            else{
                return this.createNewEntry(key, value);
            }
        }
        else{
            if(debugMode){
                log.debug("当前处于渐进式rehash阶段，额外执行一次rehash动作");
            }
            rehashToNew();

            Pair<Boolean, V> pair1 = updateTableInfo(key, value, this.table, this.capacity);
            if(pair1.getValueOne()){
                V oldValue = pair1.getValueTwo();

                if(debugMode){
                    log.debug("此次更新table操作");
                    printTable(this.table);
                }

                return oldValue;
            }

            Pair<Boolean, V> pair2 = updateTableInfo(key, value, this.rehashTable, this.rehashCapacity);
            if(pair2.getValueOne()){
                V oldValue = pair2.getValueTwo();
                if(debugMode){
                    log.debug("此次更新rehashTable操作");
                    printTable(this.rehashTable);
                }

                return oldValue;
            }

            return this.createNewEntry(key, value);
        }
    }

    private Pair<Boolean, V> updateTableInfo(K key, V value, final List<List<Entry<K, V>>> table,
                                             final int tableCapacity){
        int hash = HashUtil.hash(key);
        int index = HashUtil.indexFor(hash, tableCapacity);

        List<Entry<K, V>> entryList = new ArrayList<>();
        if(index < table.size()){
            entryList = table.get(index);
        }

        for(Entry<K, V> entry : entryList){
            final K entryKey = entry.getKey();
            if(ObjectUtil.isNull(key, entryKey) || key.equals(entryKey)){
                final V oldValue = entry.getValue();
                entry.setValue(value);
                return Pair.of(true, oldValue);
            }
        }

        return Pair.of(false, null);
    }

    private V createNewEntry(final K key, final V value){
        Entry<K, V> entry = new DefaultMapEntry<>(key, value);

        int hash = HashUtil.hash(key);

        if(isInReHash()){
            int index = HashUtil.indexFor(hash, this.rehashCapacity);
            List<Entry<K, V>> list = this.rehashTable.get(index);
            list.add(entry);

            if(debugMode){
                log.debug("目前处于rehash中，元素直接插入rehashTable中s");
                printTable(this.rehashTable);
            }
        }

        if(isNeedExpand()){
            rehash();

            int index = HashUtil.indexFor(hash, this.rehashCapacity);
            List<Entry<K, V>> list = this.rehashTable.get(index);
            list.add(entry);

            if(debugMode){
                log.debug("目前处于rehash中，元素直接插入rehashTable中");
                printTable(this.rehashTable);
            }
        }
        else{
            int index = HashUtil.indexFor(hash, this.capacity);
            List<Entry<K, V>> list = this.table.get(index);
            list.add(entry);

            if(debugMode){
                log.debug("目前不处于rehash中，元素直接插入table中");
                printTable(this.table);
            }
        }

        this.size++;
        return value;
    }

    private void rehashToNew(){
        rehashIndex++;

        List<Entry<K, V>> list = table.get(rehashIndex);
        for(Entry<K, V> entry : list){
            int hash = HashUtil.hash(entry);
            int index = HashUtil.indexFor(hash, rehashCapacity);

            List<Entry<K, V>> newList = rehashTable.get(index);

            newList.add(entry);
            rehashTable.set(index, newList);
        }

        table.set(rehashIndex, new ArrayList<Entry<K, V>> ());

        if(rehashIndex == (table.size() - 1)){
            this.capacity = this.rehashCapacity;
            this.table = this.rehashTable;

            this.rehashIndex = -1;
            this.rehashCapacity = -1;
            this.rehashTable = null;

            if(debugMode){
                log.debug("渐进式rehash已经完成");
                printTable(this.table);
            }
        }
        else{
            if(debugMode){
                log.debug("渐进式 rehash 处理中, 目前 index：{} 已完成", rehashIndex);
            }
        }
    }


    private boolean isNeedExpand(){
        double rate = (size * 1.0) / (capacity * 1.0);
        return rate >= factor && !isInReHash();
    }

    @Override
    public Set<Entry<K, V>> entrySet(){
        return null;
    }

    private boolean isInReHash(){
        return rehashIndex != -1;
    }

    private void rehash(){
        if(isInReHash()){
            if(debugMode){
                log.debug("当前处于渐进式rehash阶段，不重复进行rehash");
            }
            return;
        }

        this.rehashIndex = -1;
        this.rehashCapacity = capacity << 1;
        this.rehashTable = new ArrayList<>(this.rehashCapacity);

        for(int i = 0; i < rehashCapacity; i++){
            rehashTable.add(i, new ArrayList<Entry<K, V>>());
        }

        rehashToNew();
    }

    private void printTable(List<List<Entry<K, V>>> table){
        if(ObjectUtil.isEmpty(table)){
            return;
        }

        for(List<Entry<K, V>> list : table){
            if(CollectionUtil.isEmpty(list)){
                continue;
            }

            for(Entry<K, V> entry : list){
                System.out.print(entry + " ");
            }
            System.out.println();
        }
    }

    private void printAllTable(){
        System.out.println("原始table信息: ");
        printTable(this.table);

        System.out.println("新的table信息: ");
        printTable(this.rehashTable);
    }
}
