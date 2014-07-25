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

package org.maxur.jj.core.context;

import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;

import java.util.function.Supplier;

import static java.lang.String.format;
import static org.maxur.jj.core.domain.Role.role;

/**
 * @author Maxim Yunusov
 * @version 1.0 23.07.2014
 */
public abstract class Application {

    public static final Role APPLICATION = role("Application", Application.class);

    private static final ThreadLocal<Context> CONTEXT_HOLDER = new ThreadLocal<>();

    private Context context;

    public static Context currentContext() {
        return CONTEXT_HOLDER.get();
    }

    public static Context trunkContext() {
        final Context current = currentContext();
        final Context root = current == null ? null : current.root();
        if (root == null) {
            final Context result = new Context();
            CONTEXT_HOLDER.set(result);
            return result;
        } else {
            return root;
        }
    }

    public static Context branchContext() {
        final Context result = new Context(currentContext());
        CONTEXT_HOLDER.set(result);
        return result;
    }

    public static void closeContext() {
        CONTEXT_HOLDER.set(currentContext().parent());
    }

    public static Application configBy(final Supplier<? extends Config> supplier) throws JustJSystemException {
        try {
            return configBy(supplier.get());
        } catch (RuntimeException cause) {
            throw new JustJSystemException(format("Cannot create instance of Config with Supplier"), cause);
        }
    }

    public static Application configBy(final Config config) throws JustJSystemException {
        final Context root = new Context();
        config.config(root);
        final Application result = root.bean(APPLICATION);
        if (result == null) {
            throw new JustJSystemException("Cannot create instance of Application. " +
                    "Type of application must be described in config");
        }
        result.context = root;
        return result;
    }

    public static void runWithConfig(final Config config) {
        configBy(config).run();
    }

    public final void runWith(final String[] args) {
        openContext(context);
        preStart();
        execute(args);
        postStop();
        closeContext();
    }

    public static void openContext(final Context context) {
        CONTEXT_HOLDER.set(context == null ? new Context() : context);
    }

    protected void execute(final String[] args) {
        // It's hook
    }

    protected void preStart() {
        // It's hook
    }

    protected void postStop() {
        // It's hook
    }

    public final void run() {
        runWith(new String[]{});
    }
}
