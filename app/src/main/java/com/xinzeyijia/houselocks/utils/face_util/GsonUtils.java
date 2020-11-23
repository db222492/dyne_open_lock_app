/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.xinzeyijia.houselocks.utils.face_util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Json工具类.
 */
public class GsonUtils {
    private static Gson gson = new GsonBuilder().create();

    /**
     * 对象转json字符串
     * @param value 要转json的对象
     * @return 返回对象
     */
    public static String toJson(Object value) {
        return gson.toJson(value);
    }

    /**
     * json 字符串转对象
     * @param json 传入的json字符串
     * @param classOfT  普通对象类
     * @param <T> 对象泛型
     * @return 返回泛型指定的对象
     * @throws JsonParseException
     */
    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonParseException {
        return gson.fromJson(json, classOfT);
    }

    /**
     * json 字符串转集合对象
     * @param json 传入的json字符串
     * @param typeOfT 集合类型
     * @param <T> 集合泛型
     * @return 返回泛型指定的对象
     * @throws JsonParseException json解析异常
     */
    public static <T> T fromJson(String json, Type typeOfT) throws JsonParseException {
        return (T) gson.fromJson(json, typeOfT);
    }
}
