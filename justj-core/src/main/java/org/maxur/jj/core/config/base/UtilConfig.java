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

package org.maxur.jj.core.config.base;

import org.maxur.jj.core.context.Config;
import org.maxur.jj.core.domain.Command;
import org.maxur.jj.core.domain.CommandMapper;
import org.maxur.jj.core.domain.Executor;

import static org.maxur.jj.core.context.Application.APPLICATION;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/24/2014</pre>
 */
public class UtilConfig extends Config {

    private final Executor executor;

    public static UtilConfig runOnStart(final Executor executor) {
        return new UtilConfig(executor);
    }

    public UtilConfig(final Executor executor) {
        this.executor = executor;
    }

    @Override
    protected final void preConfig() {
        bind(APPLICATION).to(BaseApplication.class);
        bind(CommandMapper.class).to(new UtilCommander());
    }

    @Override
    protected void config() {
        // Hook
    }

    public class UtilCommander extends Commander {
        @Override
        public Command commandBy(final String[] input) {
            return command(executor);
        }
    }

}
