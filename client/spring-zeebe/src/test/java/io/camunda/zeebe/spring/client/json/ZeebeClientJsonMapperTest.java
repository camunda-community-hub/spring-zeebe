package io.camunda.zeebe.spring.client.json;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ZeebeClientJsonMapper.class)
class ZeebeClientJsonMapperTest {

    @Autowired
    private JsonMapper mapper;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private ZeebeClient client;

    @Test
    void testInjectMapper() {
        assertNotNull(mapper);
        assertInstanceOf(ZeebeClientJsonMapper.class, mapper);
    }

    @Test
    void testFromJson() {
        mapper.fromJson("{}", Object.class);
        Mockito.verify(client.getConfiguration().getJsonMapper()).fromJson("{}", Object.class);
    }

    @Test
    void testFromJsonAsMap() {
        mapper.fromJsonAsMap("{}");
        Mockito.verify(client.getConfiguration().getJsonMapper()).fromJsonAsMap("{}");
    }

    @Test
    void testFromJsonAsStringMap() {
        mapper.fromJsonAsStringMap("{}");
        Mockito.verify(client.getConfiguration().getJsonMapper()).fromJsonAsStringMap("{}");
    }

    @Test
    void testToJson() {
        mapper.toJson("");
        Mockito.verify(client.getConfiguration().getJsonMapper()).toJson("");
    }

    @Test
    void testValidateJson() {
        mapper.validateJson("variables", "{}");
        Mockito.verify(client.getConfiguration().getJsonMapper()).validateJson("variables", "{}");
    }

    @Test
    void testValidateJsonInputStream() {
        ByteArrayInputStream jsonInputStream = new ByteArrayInputStream("{}".getBytes());
        mapper.validateJson("variables", jsonInputStream);
        Mockito.verify(client.getConfiguration().getJsonMapper()).validateJson("variables", jsonInputStream);
    }

}
