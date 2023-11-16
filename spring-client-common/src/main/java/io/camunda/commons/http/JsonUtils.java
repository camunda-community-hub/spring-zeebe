package io.camunda.commons.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

    private JsonUtils() {
    }

    private static ObjectMapper mapper;

    private static Map<Class<?>, JavaType> searchResultTypeMap = new HashMap<>();

    private static ObjectMapper getObjectMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return mapper;
    }

    public static JsonNode toJsonNode(InputStream is) throws IOException {
        return getObjectMapper().readTree(is);
    }

    public static JsonNode toJsonNode(String json) throws IOException {
        return getObjectMapper().readTree(json);
    }

    public static String toJson(Object object) throws IOException {
        return getObjectMapper().writeValueAsString(object);
    }

    public static <T> T toResult(String json, Class<T> resultType) throws IOException {
        return getObjectMapper().readValue(json, resultType);
    }

  public static <T, U> T toParameterizedTypeResult(String json, Class<T> resultType, Class<U> parameterType) throws IOException {
      JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(resultType, parameterType);
      return getObjectMapper().readValue(json, javaType);
  }

//    public static <T> SearchResult<T> toSearchResult(String json, Class<T> resultType) throws IOException {
//        return getObjectMapper().readValue(json, getSearchResultType(resultType));
//    }

//    private static JavaType getSearchResultType(Class<?> resultType) {
//        if (!searchResultTypeMap.containsKey(resultType)) {
//            searchResultTypeMap.put(resultType,
//                    getObjectMapper().getTypeFactory().constructParametricType(SearchResult.class, resultType));
//        }
//        return searchResultTypeMap.get(resultType);
//    }

}
