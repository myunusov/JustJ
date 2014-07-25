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

package org.maxur.jj.core.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.context.Application;
import org.maxur.jj.core.context.Config;
import org.maxur.jj.core.context.Context;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.maxur.jj.core.context.Application.APPLICATION;
import static org.maxur.jj.core.context.Application.branchContext;
import static org.maxur.jj.core.context.Application.currentContext;
import static org.maxur.jj.core.domain.Command.command;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTest {

    private DummyApplication application;
    private DummyConfig config;
    @Mock
    private List fake;

    @Before
    public void setUp() throws Exception {
        config = spy(new DummyConfig());
        application = spy(new DummyApplication());
    }

    @After
    public void tearDown() throws Exception {
        Application.closeContext();
    }

    @Test
    public void testSystem() throws Exception {
        assertNotNull(Application.system());
    }

    @Test
    public void testConfigBy() throws Exception {
        assertNotNull(Application.configBy(config));
        verify(config).run();
    }

    @Test
    public void testConfigByWithEmptyConfig() throws Exception {
        assertNotNull(Application.configBy(config));
        verify(config).run();
    }

    @Test
    public void testConfigByWithApplicationBean() throws Exception {
        final Answer answer = invocation -> {
            config.bind(APPLICATION).to(application);
            return null;
        };
        doAnswer(answer).when(config).config();
        assertEquals(application, Application.configBy(config));
    }


    @Test(expected = JustJSystemException.class)
    public void testConfigByWithWrongSupplier() throws Exception {
        Application.configBy(this::make);
    }

    @Test
    public void testRun() throws Exception {
        application.run();
        verify(application).execute(any());
    }

    @Test
    public void testRunWithArgs() throws Exception {
        application.runWith(new String[] {"a"});
        verify(application).execute(any());
    }

    @Test
    public void testRunWithCommand() throws Exception {
        application.runWith(command(fake::size));
        verify(fake).size();
    }

    @Test
    public void testRunWithExecutor() throws Exception {
        application.runWith(fake::size);
        verify(fake).size();
    }

    @Test
    public void testBranchContext() throws Exception {
        final Context root = currentContext();
        final Context context = branchContext();
        assertEquals(root, context.parent());
        Application.closeContext();
    }

    private Config make() {
        return null;
    }

    private static class DummyConfig extends Config {
        @Override
        protected void config() {
            run();
        }
        public void run() {
        }
    }

    static class DummyApplication extends Application {
        @Override
        protected void execute(String[] args) {
        }
    }
}