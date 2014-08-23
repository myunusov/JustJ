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

import static org.maxur.jj.core.domain.Role.role;

/**
 * This class is context container and it's response is project lifecycle.
 *
 * @author Maxim Yunusov
 * @version 1.0 23.07.2014
 */
public abstract class Application {

    public static final Role<Application> APPLICATION = role("Application", Application.class);

    private static final ThreadLocal<Scope> CONTEXT_HOLDER = new ThreadLocal<>();

    public static Scope currentScope() {
        final Scope result = CONTEXT_HOLDER.get();
        if (result != null) {
            return result;
        }
        final Scope scope = new BaseScope();
        CONTEXT_HOLDER.set(scope);
        return scope;
    }

    public static Scope branchScope() {
        final Scope result = currentScope().branch() ;
        CONTEXT_HOLDER.set(result);
        return result;
    }

    public static void closeContext() {
        CONTEXT_HOLDER.set(currentScope().parent());
    }

    public static Application configBy(final Supplier<? extends Config> supplier)  {
        try {
            return configBy(supplier.get());
        } catch (RuntimeException cause) {
            throw new JustJSystemException("Cannot create instance of Config with Supplier", cause);
        }
    }

    public static Application configBy(final Config config)  {
        final Scope scope = currentScope();
        scope.accept(config);
        return getApplication(scope);
    }

    public static Application system() {
        return getApplication(currentScope());
    }

    private static Application getApplication(final Scope scope) {
        final Application application = scope.bean(APPLICATION);
        if (application == null) {
            final Application result = new Application() {
            };
            currentScope().accept(
                    new Config() {
                        @Override
                        protected void config() {
                            bind(APPLICATION).to(result);
                        }
                    }
            );
            return result;
        }
        return application;
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
        currentScope().inject(executor);
        executor.run();
    }
}
