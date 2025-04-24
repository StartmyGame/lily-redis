package com.lily.redis.model;


import java.util.Arrays;

/**
 *
 * AOF持久化明细
 *
 */
public class PersistAofEntry {

    /**
     *
     * 参数信息
     *
     */
    private Object[] params;

    /**
     *
     * 方法名称
     *
     */
    private String methodName;

    /**
     *
     * 新建实例
     *
     * @return
     */
    public static PersistAofEntry newInstance(){
        return new PersistAofEntry();
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "PersistAofEntry{" +
                "params=" + Arrays.toString(params) +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
