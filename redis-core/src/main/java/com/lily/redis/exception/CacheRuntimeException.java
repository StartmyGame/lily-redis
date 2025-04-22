package com.lily.redis.exception;

public class CacheRuntimeException extends RuntimeException{
    public CacheRuntimeException(String message) {
        super(message);
    }
}
