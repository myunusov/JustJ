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

package org.maxur.jj.core.it;

import org.junit.Before;
import org.junit.Test;
import org.maxur.jj.core.annotation.Optional;
import org.maxur.jj.core.domain.Command;

import javax.inject.Inject;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.maxur.jj.core.context.Application.branchScope;
import static org.maxur.jj.core.context.Application.closeScope;
import static org.maxur.jj.core.context.Application.configBy;
import static org.maxur.jj.core.context.Application.currentScope;

/**
 * @author Maxim Yunusov
 * @version 1.0 29.07.2014
 */
public class InjectIT {

    private DummyApp app;

    @Before
    public void setUp() throws Exception {
        app = new DummyApp();
    }

    @Test
    public void testInjectWithSimpleConfig() throws Exception {
        branchScope();
        configBy(
                c -> c.bind(String.class).to("World")
        ).runWith(app);
        assertEquals("Hello World", app.result);
        closeScope();
    }

    @Test
    public void testInjectWithSimpleConfigAndScope() throws Exception {
        assertNull(app.value1);
        branchScope();
        configBy(
                c -> c.bind(String.class).to("World")
        ).runWith(app);
        assertEquals("Hello World", app.result);
        closeScope();
        currentScope().inject(app);
        assertNull(app.value1);
    }

    @Test
    public void testInjectWithSimpleConfigAndMultiplyConfig() throws Exception {
        branchScope();
        configBy(
                c -> c.bind(String.class).to("World")
        );
        configBy(
                c -> c.bind(Integer.class).to(1)
        );
        currentScope().inject(app);
        assertEquals("World", app.value1);
        assertEquals(new Integer(1), app.value2);
        closeScope();
    }


}

class DummyApp extends Command  {

    public String result;

    @Inject
    @Optional
    public String value1;

    @Inject
    @Optional
    public Integer value2;

    @Override
    public void run() {
        result = format("Hello %s", value1);
    }
}

