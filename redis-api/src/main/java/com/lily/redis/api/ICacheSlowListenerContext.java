package com.lily.redis.api;


/**
 *
 * 慢日志监听器上下文
 *
 */
public interface ICacheSlowListenerContext {

    /**
     *
     * 方法名称
     *
     * @return
     */
    String methodName();

    /**
     *
     * 参数信息
     *
     * @return
     */
    Object[] params();

    /**
     *
     * 方法结果
     *
     * @return
     */
    Object result();

    /**
     *
     * 开始时间
     *
     * @return
     */
    long startTimeMills();

    /**
     *
     * 结束时间
     *
     * @return
     */
    long endTimeMills();

    /**
     *
     * 消耗时间
     *
     * @return
     */
    long costTimeMills();
}
