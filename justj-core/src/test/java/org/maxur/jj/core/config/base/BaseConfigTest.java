/*
 * Copyright (c) 2014 Maxim Yunusov
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.maxur.jj.core.config.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.context.Config;
import org.maxur.jj.core.domain.JustJSystemException;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BaseConfigTest {

    @Spy
    private DummyConfig config = new DummyConfig();

    @Test
    public void testCreateEmptyConfig() throws Exception {
        assertNotNull(BaseApplication.configBy(() -> config));
        verify(config).run();
    }

    @Test(expected = JustJSystemException.class)
    public void testCreateConfigWithWrongSupplier() throws Exception {
        BaseApplication.configBy(this::make);
    }

    private Config make() {
        return null;
    }

    private static class DummyConfig extends BaseConfig {
        @Override
        protected void config() {
            run();
        }
        public void run() {
        }
    }
}