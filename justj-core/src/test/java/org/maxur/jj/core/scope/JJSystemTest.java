/*
 * Copyright (c) 2014 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.maxur.jj.core.scope;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.config.Configuration;
import org.maxur.jj.core.entity.Command;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.maxur.jj.core.config.Role.HOME_VIEW;
import static org.maxur.jj.core.scope.JJSystem.configBy;
import static org.maxur.jj.core.scope.JJSystem.system;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JJSystemTest {

    @Mock
    private static JJView view;
    @Mock
    private Command command;

    @Test
    public void testSystemConfigIsCreated() throws Exception {
        final JJSystem system = system(configBy(DummyConfiguration.class));
        assertNotNull(system);
    }

    @Test
    public void testSystemRunWithDefaultConfig() throws Exception {
        final JJSystem system = system(configBy(DummyConfiguration.class));
        when(command.isApplicableTo(any())).thenReturn(true);
        system.tell(command);
        verify(command).visit(system);
    }

    private static class DummyConfiguration extends Configuration {

        public DummyConfiguration() {
        }

        @Override
        public void config() {
            bind(HOME_VIEW).to(view);
        }

    }
}