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

import org.maxur.jj.core.domain.Command;
import org.maxur.jj.core.domain.Executor;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;

import java.util.function.Supplier;

import static java.lang.String.format;
import static org.maxur.jj.core.domain.Role.role;

/**
 * This class is context container and it's response is project lifecycle.
 *
 * @author Maxim Yunusov
 * @version 1.0 23.07.2014
 */
public abstract class Application {

    public static final Role APPLICATION = role("Application", Application.class);

    private static final ThreadLocal<Context> CONTEXT_HOLDER = new ThreadLocal<>();

    public static Context currentContext() {
        final Context result = CONTEXT_HOLDER.get();
        if (result != null) {
            return result;
        }
        final Context context = new Context();
        CONTEXT_HOLDER.set(context);
        return context;
    }

    public static Context branchContext() {
        final Context result = new Context(currentContext());
        CONTEXT_HOLDER.set(result);
        return result;
    }

    public static void closeContext() {
        CONTEXT_HOLDER.set(currentContext().parent());
    }

    public static Application configBy(final Supplier<? extends Config> supplier)  {
        try {
            return configBy(supplier.get());
        } catch (RuntimeException cause) {
            throw new JustJSystemException(format("Cannot create instance of Config with Supplier"), cause);
        }
    }

    public static Application configBy(final Config config)  {
        final Context context = currentContext();
        config.applyTo(context);
        return getApplication(context);
    }

    public static Application system() {
        return getApplication(currentContext());
    }

    private static Application getApplication(final Context context) {
        Application application = context.bean(APPLICATION);
        if (application == null ) {
            application = new Application() {
            };
            context.put(APPLICATION, application);
        }
        return context.inject(application);

    }

    public final void runWith(final String[] args) {
        preStart();
        execute(args);
        postStop();
        shutdown();
    }

    public void shutdown() {
        closeContext();
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

    public void runWith(final Executor executor) {
        executor.run();
    }

    public void runWith(final Command executor) {
        currentContext().inject(executor);
        executor.run();
    }
}
