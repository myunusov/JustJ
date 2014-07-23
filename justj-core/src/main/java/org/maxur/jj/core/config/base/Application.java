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

import org.maxur.jj.core.context.Config;
import org.maxur.jj.core.context.Context;
import org.maxur.jj.core.domain.CommandMapper;
import org.maxur.jj.core.domain.Inject;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;

import java.util.function.Supplier;

import static java.lang.String.format;
import static org.maxur.jj.core.domain.Role.role;

/**
 * @author Maxim Yunusov
 * @version 1.0 18.07.2014
 */
public class Application {

    public static final Role APPLICATION = role("Application", Application.class);

    public static Application configBy(
            final Supplier<? extends Config> supplier
    ) throws JustJSystemException {
        try {
            final Config config = supplier.get();
            config.config(Context.root());
            return Context.current().bean(APPLICATION);
        } catch (RuntimeException cause) {
            throw new JustJSystemException(
                    format("Cannot create instance of Config with Supplier '%s'", supplier.toString()),
                    cause
            );
        }
    }


    private final CommandMapper<String[]> commandMapper;

    @Inject
    public Application(final CommandMapper<String[]> commandMapper) {
        this.commandMapper = commandMapper;
    }

    public void runWith(final String[] args) {
        commandMapper.commandBy(args).execute();
    }

}
