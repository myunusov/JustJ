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

import org.maxur.jj.core.domain.Command;
import org.maxur.jj.core.domain.CommandMapper;
import org.maxur.jj.core.domain.Context;

/**
 * @author Maxim Yunusov
 * @version 1.0 18.07.2014
 */
public final class BaseCommandMapper extends CommandMapper<String[]> {

    //@Inject
    //private final Context context;  // TODO Must be injected

    @Override
    public Command commandBy(final String[] input) {
        Context context = new Context(); // TODO Stub
        return context.command(c -> {
            System.out.print("Hello Word");
        }); // TODO  stub
    }
}
