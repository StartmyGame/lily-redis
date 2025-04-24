package com.lily.redis.support.persist;

import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.lily.redis.api.ICache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * 缓存持久化-AOF
 *
 * @param <K>
 * @param <V>
 */
public class CachePersistAof<K, V> extends CachePersistAdaptor<K, V>{

    private static final Log log = LogFactory.getLog(CachePersistAof.class);


    /**
     *
     * 缓存列表
     *
     */
    private final List<String> bufferList = new ArrayList<>();

    /**
     *
     * 数据持久化路径
     *
     *
     */
    private final String dbPath;

    public CachePersistAof(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void persist(ICache<K, V> cache){
        log.info("开始 AOF 持久化到文本");

        if(!FileUtil.exists(dbPath)){
            FileUtil.createFile(dbPath);
        }

        FileUtil.append(dbPath, bufferList);

        bufferList.clear();
        log.info("完成 AOF 持久化到文件");
    }

    @Override
    public long delay(){
        return 1;
    }

    @Override
    public long period(){
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }

    /**
     *
     * 添加文件至buffer列表中
     *
     * @param json
     */
    public void append(final String json){
        if(StringUtil.isNotEmpty(json)){
            bufferList.add(json);
        }
    }
}
