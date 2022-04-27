package io.camunda.zeebe.spring.client.json;

import java.io.InputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;

/**
 * Provides the {@link JsonMapper} of the {@link ZeebeClient}
 * as a Spring Bean that can be injected,
 * e.g. for logging or debugging variable objects.
 * 
 * <p>Usage example:
 * <pre>
 *{@code @Autowired}
 *private JsonMapper mapper;
 * 
 *{@code @ZeebeWorker(type = "my-service")}
 *public void invokeMyService(@ZeebeVariablesAsType ProcessVariables variables) {
 *  LOG.info("Invoking myService with variables: " + mapper.toJson(variables));
 *}
 * </pre>
 */
@Component
public class ZeebeClientJsonMapper implements JsonMapper {
    
    @Autowired
    ZeebeClient client;
    
    private JsonMapper getMapper() {
        return client.getConfiguration().getJsonMapper();
    }
    
    @Override
    public <T> T fromJson(String json, Class<T> typeClass) {
        return getMapper().fromJson(json, typeClass);
    }    

    @Override
    public Map<String, Object> fromJsonAsMap(String json) {
        return getMapper().fromJsonAsMap(json);
    }    

    @Override
    public Map<String, String> fromJsonAsStringMap(String json) {
        return getMapper().fromJsonAsStringMap(json);
    }    

    @Override
    public String toJson(Object value) {
        return getMapper().toJson(value);
    }    

    @Override
    public String validateJson(String propertyName, String jsonInput) {
        return getMapper().validateJson(propertyName, jsonInput);
    }

    @Override
    public String validateJson(String propertyName, InputStream jsonInput) {
        return getMapper().validateJson(propertyName, jsonInput);
    }
    
}
