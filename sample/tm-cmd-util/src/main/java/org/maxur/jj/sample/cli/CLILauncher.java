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

package org.maxur.jj.sample.cli;

import org.maxur.jj.core.config.base.BaseConfig;
import org.maxur.jj.core.config.base.Commander;
import org.maxur.jj.core.domain.Command;
import org.maxur.jj.core.domain.CommandMapper;

import static java.lang.System.out;
import static org.maxur.jj.core.config.base.BaseApplication.configBy;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public final class CLILauncher {

    private CLILauncher() {
    }

    public static void main(final String[] args) {
        configBy(TMConfig::new).runWith(args);
    }

    public static class TMCommander extends Commander {
        @Override
        public Command commandBy(final String[] input) {
            return command(() -> out.print("Hello World"));
        }
    }

    public static class TMConfig extends BaseConfig {
        @Override
        protected void config() {
            bind(CommandMapper.class).to(TMCommander.class);
        }
    }
}
