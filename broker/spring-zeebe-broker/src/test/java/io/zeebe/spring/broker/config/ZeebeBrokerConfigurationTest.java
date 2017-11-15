/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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