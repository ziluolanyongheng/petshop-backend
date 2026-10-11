package com.petshop.petshop_backend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 对象转JSON字符串
    public static String toJson(Object object) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(object);

    }

    // 对象转JSON字符串（美化格式）
    public static String toPrettyJson(Object object) throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    // JSON字符串转复杂对象（如List、Map等）
    public static <T> T fromJson(String json, TypeReference<T> typeReference) throws Exception {
        return objectMapper.readValue(json, typeReference);
    }
}
