package com.lily.redis.annotation;


import java.lang.annotation.*;

/**
 * 缓存拦截器
 */

@Documented // @Documented 注解用于指定被修饰的注解会被 JavaDoc 工具提取到文档中。
@Inherited // @Inherited 注解允许被修饰的注解具有继承性。子类的相同函数具有相同的注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheInterceptor {

    /**
     * 通用拦截器
     *
     * 1、 耗时统计
     * 2、 慢日志统计
     *
     * @return 默认开启
     */
    boolean common() default true;

    /**
     *
     * 是否启用刷新
     *
     * @return
     */
    boolean refresh() default false;


    /**
     *
     * 操作是否需要 append to file，默认为 false
     * 主要针对 cache 内容有变更的操作，不包括查询操作。
     * 包括删除，添加，过期等操作。
     *
     * @return
     */
    boolean aof() default false;

    /**
     *
     * 是否执行驱逐更新
     *
     * @return
     */
    boolean evict() default false;
}
