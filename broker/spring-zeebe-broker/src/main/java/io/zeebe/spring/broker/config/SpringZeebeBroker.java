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

import io.zeebe.broker.Broker;
import io.zeebe.broker.system.ConfigurationManager;
import org.springframework.context.SmartLifecycle;

import java.util.function.Supplier;

public class SpringZeebeBroker implements SmartLifecycle, Supplier<Broker>
{

    public static final int PHASE = 1000;

    private final ConfigurationManager configurationManager;

    /**
     * Late init during {@link #start()}.
     */
    private Broker broker;

    public SpringZeebeBroker(final ConfigurationManager configurationManager)
    {
        this.configurationManager = configurationManager;
    }

    @Override
    public void start()
    {
        broker = new Broker(configurationManager);
    }

    @Override
    public boolean isRunning()
    {
        return broker != null;
    }

    @Override
    public boolean isAutoStartup()
    {
        return true;
    }

    @Override
    public void stop(final Runnable callback)
    {
        broker.close();
        callback.run();
    }

    @Override
    public void stop()
    {
        this.stop(() -> {
        });
    }

    @Override
    public int getPhase()
    {
        return PHASE;
    }

    @Override
    public Broker get()
    {
        if (!isRunning())
        {
            throw new IllegalStateException("broker is not running!");
        }

        return broker;
    }
}
