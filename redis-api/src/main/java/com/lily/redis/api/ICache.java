package com.lily.redis.api;

import java.util.List;
import java.util.Map;

public interface ICache<K, V> extends Map<K, V> {


    /**
     *
     * 删除监听类列表
     *
     * @return
     */
    List<ICacheRemoveListener<K, V>> removeListeners();

}
