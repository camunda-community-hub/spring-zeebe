package io.zeebe.spring.client.properties;

import io.zeebe.client.ZeebeClientConfiguration;
import io.zeebe.client.impl.ZeebeClientBuilderImpl;

public interface ZeebeClientProperties extends ZeebeClientConfiguration
{

    ZeebeClientBuilderImpl DEFAULT = null; //FIXME: ZeebeClientBuilderImpl.fromProperties(new Properties());

    default boolean isAutoStartup()
    {
        return true;
    }

}
