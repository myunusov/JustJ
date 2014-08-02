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

package org.maxur.jj.sample.hello;

import org.maxur.jj.core.config.base.SimpleConfig;
import org.maxur.jj.core.domain.Command;

import javax.inject.Inject;

import static java.lang.String.format;
import static java.lang.System.out;
import static org.maxur.jj.core.context.Application.configBy;

/**
 * IoC Container and Injector.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/25/2014</pre>
 */
public class HelloWorldInjector extends Command {

    @Inject
    private String value;

    public static void main(String[] args) {
        configBy(new SimpleConfig()
                .config(c -> c.bind(String.class).to("World"))
        ).runWith(new HelloWorldInjector());
    }

    @Override
    public void run() {
        out.println(format("Hello %s", value));
    }

}
