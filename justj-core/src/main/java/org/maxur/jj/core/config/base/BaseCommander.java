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

import org.maxur.jj.core.context.Context;
import org.maxur.jj.core.domain.Command;
import org.maxur.jj.core.domain.CommandMapper;
import org.maxur.jj.core.domain.Executor;

import static org.maxur.jj.core.config.base.BaseContext.current;

/**
 * @author Maxim Yunusov
 * @version 1.0 18.07.2014
 */
public final class BaseCommander implements CommandMapper<String[]> {

    public Command command(final Executor executor) {
        return new Command() {

            protected void run() {
                executor.run();
            }

            public final void execute() {
                final Context context = current().branch();
                context.inject(this);
                run();
                context.stop();
            }
        };
    }

    @Override
    public Command commandBy(final String[] input) {
        return command(() -> System.out.print("Hello Word")); // TODO  stub
    }

}
