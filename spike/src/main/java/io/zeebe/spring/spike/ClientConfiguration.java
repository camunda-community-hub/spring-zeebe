package io.zeebe.spring.spike;

public interface ClientConfiguration {

    String getHost();
    int getPort();

    boolean isAutoStartup();

}
