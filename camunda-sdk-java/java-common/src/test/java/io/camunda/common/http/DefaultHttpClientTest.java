package io.camunda.common.http;

import com.google.common.reflect.TypeToken;
import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultHttpClientTest {


  @Mock
  Authentication authentication;
  @Mock
  CloseableHttpClient chClient;
  JsonMapper jsonMapper = new SdkObjectMapper();

  @Test
  public void shouldReturnGetType() throws IOException {
    // given
    Map<Product, Map<Class<?>, String>> productMap = new HashMap<>();
    DefaultHttpClient defaultHttpClient = new DefaultHttpClient(authentication, chClient, jsonMapper, productMap);
    CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
    MyTestClass expectedOutput = new MyTestClass();
    expectedOutput.setName("test-name");

    // when
    when(chClient.execute(any(HttpGet.class))).thenReturn(response);
    when(authentication.getTokenHeader(any())).thenReturn(new AbstractMap.SimpleEntry<>("key", "value"));
    when(response.getEntity().getContent()).thenReturn(new ByteArrayInputStream("{\"name\" : \"test-name\"}".getBytes()));
    MyTestClass parsedResponse = defaultHttpClient.get(MyTestClass.class, "123");

    // then
    assertTrue(new ReflectionEquals(expectedOutput).matches(parsedResponse));
  }

  @Test
  public void shouldReturnPostType() throws IOException {
    // given
    Map<Product, Map<Class<?>, String>> productMap = new HashMap<>();
    DefaultHttpClient defaultHttpClient = new DefaultHttpClient(authentication, chClient, jsonMapper, productMap);
    CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
    MyTestClass insideClass = new MyTestClass();
    insideClass.setName("test-name");
    List<MyTestClass> expectedOutput = new ArrayList<>();
    expectedOutput.add(insideClass);

    // when
    when(chClient.execute(any(HttpPost.class))).thenReturn(response);
    when(authentication.getTokenHeader(any())).thenReturn(new AbstractMap.SimpleEntry<>("key", "value"));
    when(response.getEntity().getContent()).thenReturn(new ByteArrayInputStream("[{\"name\" : \"test-name\"}]".getBytes()));
    List parsedResponse = defaultHttpClient.post(List.class, MyTestClass.class, TypeToken.of(MyTestClass.class), "dummy");

    // then
    assertEquals(expectedOutput.size(), parsedResponse.size());
    assertTrue(new ReflectionEquals(expectedOutput.get(0)).matches(parsedResponse.get(0)));
  }

  public static class MyTestClass {
    private String name;
    public void setName(String name) {
      this.name = name;
    }
  }
}
