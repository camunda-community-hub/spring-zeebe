package io.zeebe.spring.broker.config;

import org.junit.Test;
import org.springframework.core.env.Environment;

import static io.zeebe.spring.broker.config.ZeebeBrokerConfiguration.tomlFileFromEnv;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZeebeBrokerConfigurationTest {

    private static String[] EMPTY = new String[0];

    private final Environment environment = mock(Environment.class);

    @Test
    public void tomlFileFromEnv_env_empty() throws Exception {
        assertThat(tomlFileFromEnv.apply(environment)).isEmpty();
    }

    @Test
    public void tomlFileFromEnv_env_no_arg() throws Exception {
        when(environment.getProperty("nonOptionArgs", String[].class, EMPTY)).thenReturn(EMPTY);

        assertThat(tomlFileFromEnv.apply(environment)).isEmpty();
    }

    @Test
    public void tomlFileFromEnv_env_single_Arg() throws Exception {
        when(environment.getProperty("nonOptionArgs", String[].class, EMPTY)).thenReturn(new String[]{"foo"});

        assertThat(tomlFileFromEnv.apply(environment)).hasValue("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tomlFileFromEnv_env_multi_Args() throws Exception {
        when(environment.getProperty("nonOptionArgs", String[].class, EMPTY)).thenReturn(new String[]{"foo", "bar"});

        tomlFileFromEnv.apply(environment);
    }
}