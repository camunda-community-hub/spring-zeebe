package io.camunda.commons.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Java8Utils {

  private Java8Utils() {

  }

  public static byte[] readAllBytes(InputStream inputStream) throws IOException {
    final int bufLen = 4 * 0x400; // 4KB
    byte[] buf = new byte[bufLen];
    int readLen;
    IOException exception = null;

    try {
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
          outputStream.write(buf, 0, readLen);

        return outputStream.toByteArray();
      }
    } catch (IOException e) {
      exception = e;
      throw e;
    } finally {
      if (exception == null) inputStream.close();
      else try {
        inputStream.close();
      } catch (IOException e) {
        exception.addSuppressed(e);
      }
    }
  }

  public static Map<Class<?>, String> toMap(Object... array) {
    AbstractMap.SimpleEntry<Class<?>, String>[] entryArray = new AbstractMap.SimpleEntry[array.length/2];
    for(int i=0;i<entryArray.length;i++) {
      entryArray[i]= new  AbstractMap.SimpleEntry<Class<?>, String>((Class<?>)array[i*2], (String) array[i*2+1]);
    }
    return Stream.of(entryArray)
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
