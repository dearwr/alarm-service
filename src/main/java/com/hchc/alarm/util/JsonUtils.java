package com.hchc.alarm.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    // json 序列化
    public static String toJson(Object src) {
        if (null != mapper) {
            if (src instanceof String) {
                return (String) src;
            } else {
                try {
                    return mapper.writeValueAsString(src);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    // json 序列化
    public static <T> T toRead(String json, Class<T> clz) {
        try {
            return mapper.readValue(json, clz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // json 序列化
    public static <T> T toRead(JsonNode json, Class<T> clz) {
        try {
            return mapper.treeToValue(json,clz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> parseList(String jsonData, Class<T> clz) {
        JavaType jt = mapper.getTypeFactory().constructParametricType(ArrayList.class, clz);
        List<T> urlist = null;
        try {
            urlist = mapper.readValue(jsonData, jt);
        } catch (IOException e) {
            urlist = new ArrayList<>();
        }
        return urlist;
    }

    public static <T> List<T> readToList(String json, Class<T[]> clz){
        T[] ts = toRead(json, clz);
        if (ts == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(ts));
    }

    public static JsonNode read(String text) throws IOException{
        return mapper.readTree(text);
    }

    public static JsonNode read(Map<String, JsonNode> data){
        return mapper.valueToTree(data);
    }

    public static ObjectNode createNode(){
        return mapper.createObjectNode();
    }

    public static ArrayNode createArrayNode(){
        return mapper.createArrayNode();
    }
}
