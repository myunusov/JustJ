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
import org.maxur.jj.core.domain.Application;
import org.maxur.jj.core.domain.CommandMapper;
import org.maxur.jj.core.domain.Inject;
import org.maxur.jj.core.domain.JustJSystemException;

import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Hold lifecycle of application.
 *
 * @author Maxim Yunusov
 * @version 1.0 18.07.2014
 */
public class BaseApplication implements Application {

    private final CommandMapper<String[]> commandMapper;

    public static BaseApplication configBy(
            final Supplier<? extends Config> supplier
    ) throws JustJSystemException {
        try {
            final Config config = supplier.get();
            config.config(Context.trunk());
            return Context.current().bean(APPLICATION);
        } catch (RuntimeException cause) {
            throw new JustJSystemException(format("Cannot create instance of Config with Supplier"), cause);
        }
    }

    public static BaseApplication configBy(final Config config) throws JustJSystemException {
        config.config(Context.trunk());
        return Context.current().bean(APPLICATION);
    }

    public static void runWithConfig(final BaseConfig config) {
        configBy(config).run();
    }

    @Override
    public final void run() {
        runWith(new String[]{});
    }

    @Override
    public final void runWith(final String[] args) {
        preStart();
        commandMapper.commandBy(args).execute();
        postStop();
        Context.current().stop();
    }


    @Inject
    public BaseApplication(final CommandMapper<String[]> commandMapper) {
        this.commandMapper = commandMapper;
    }


    protected void postStop() {
        // It's hook
    }

    protected void preStart() {
        // It's hook
    }

}
