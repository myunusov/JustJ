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
import org.maxur.jj.core.domain.JustJSystemException;

import java.util.function.Supplier;

import static java.lang.String.format;
import static org.maxur.jj.core.domain.Application.APPLICATION;

/**
 * @author Maxim Yunusov
 * @version 1.0 23.07.2014
 */
public class BaseContext extends Context<BaseContext> {

    private static final ThreadLocal<BaseContext> contextHolder = new ThreadLocal<>();

    public static BaseContext current() {
        return contextHolder.get();
    }

    public static BaseContext configBy(
            final Supplier<? extends Config> supplier
    ) throws JustJSystemException {
        try {
            final Config config = supplier.get();
            config.config(BaseContext.trunk());
            return BaseContext.current();
        } catch (RuntimeException cause) {
            throw new JustJSystemException(format("Cannot create instance of Config with Supplier"), cause);
        }
    }

    public static Context trunk() {
        final BaseContext current = current();
        final Context root = current == null ? null : current.root();
        if (root == null) {
            final BaseContext result = new BaseContext();
            contextHolder.set(result);
            return result;
        } else {
            return root;
        }
    }

    private BaseContext() {
    }

    private BaseContext(final BaseContext parent) {
        super(parent);
    }

    public Application system() {
        return BaseContext.current().bean(APPLICATION);
    }

    public BaseContext branch() {
        final BaseContext result = new BaseContext(this);
        contextHolder.set(result);
        return result;
    }

    public void stop() {
        contextHolder.set(parent());
    }
}
