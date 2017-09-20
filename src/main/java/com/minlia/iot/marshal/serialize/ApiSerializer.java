package com.minlia.iot.marshal.serialize;


import com.minlia.iot.scope.HttpMediaType;

/**
 * 序列器
 */
public interface ApiSerializer<T>   {
    String serialize(T t, HttpMediaType mediaType);
}
