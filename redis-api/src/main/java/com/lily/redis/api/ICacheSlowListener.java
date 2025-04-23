package com.lily.redis.api;


/**
 *
 * 慢日志操作接口
 *
 */
public interface ICacheSlowListener {

    /**
     *
     * 监听
     *
     * @param context
     */
    void listen(final ICacheSlowListenerContext context);

    /**
     *
     * 慢日志的阈值
     *
     * @return
     */
    long slowerThanMills();
}
