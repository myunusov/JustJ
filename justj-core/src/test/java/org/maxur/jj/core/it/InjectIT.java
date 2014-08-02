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

import org.junit.Test;
import org.maxur.jj.core.config.base.SimpleConfig;
import org.maxur.jj.core.domain.Command;

import javax.inject.Inject;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.maxur.jj.core.context.Application.configBy;

/**
 * @author Maxim Yunusov
 * @version 1.0 29.07.2014
 */
public class InjectIT {

    @Test
    public void testInject() throws Exception {
        final DummyApp app = new DummyApp();
        configBy(new SimpleConfig()
                        .config(c -> c.bind(String.class).to("World"))
        ).runWith(app);
        assertEquals("Hello World", app.result);
    }
}

class DummyApp extends Command  {

    public String result;

    @Inject
    private String value;

    @Override
    public void run() {
        result = format("Hello %s", value);
    }

}

