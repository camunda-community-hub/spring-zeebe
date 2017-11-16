package io.zeebe.spring.broker.config;

import org.junit.Test;
import org.springframework.core.env.Environment;

import static io.zeebe.spring.broker.config.ZeebeBrokerConfiguration.tomlFileFromEnv;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZeebeBrokerConfigurationTest
{

    private static final String[] EMPTY = new String[0];

    private final Environment environment = mock(Environment.class);

    @Test
    public void tomlFileFromEnvEnvEmpty() throws Exception
    {
        assertThat(tomlFileFromEnv.apply(environment)).isEmpty();
    }

    @Test
    public void tomlFileFromEnvEnvNoArg() throws Exception
    {
        when(environment.getProperty("nonOptionArgs", String[].class, EMPTY)).thenReturn(EMPTY);

        assertThat(tomlFileFromEnv.apply(environment)).isEmpty();
    }

    @Test
    public void tomlFileFromEnEnvSingleArg() throws Exception
    {
        when(environment.getProperty("nonOptionArgs", String[].class, EMPTY)).thenReturn(new String[]{"foo"});

        assertThat(tomlFileFromEnv.apply(environment)).hasValue("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tomlFileFromEnvEnvMultiArgs() throws Exception
    {
        when(environment.getProperty("nonOptionArgs", String[].class, EMPTY)).thenReturn(new String[]{"foo", "bar"});

        tomlFileFromEnv.apply(environment);
    }
}
