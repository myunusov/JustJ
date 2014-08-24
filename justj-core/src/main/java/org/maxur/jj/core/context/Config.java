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

package org.maxur.jj.core.context;

import org.maxur.jj.core.domain.Entity;
import org.maxur.jj.core.domain.Role;

import java.util.function.Supplier;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public abstract class Config extends Entity {

    private Context context;

    public final void applyTo(final Context context) {
        this.context = context;
        preConfig();
        config();
        postConfig();
    }

    protected abstract void config();

    protected void postConfig() {
        //  Hook
    }

    protected void preConfig() {
        //  Hook
    }

    public <T> Binder<T> bind(final Role<T> role) {
        return new RoleBinder<>(context, role);
    }

    public <T> Binder<T> bind(Class<T> type) {
        return new TypeBinder<>(context, type);
    }

    public abstract static class Binder<T> {

        protected final Context context;

        protected Binder(final Context context) {
            this.context = context;
        }

        public abstract void to(Supplier<? extends T> supplier);

        public abstract void to(T bean);

        public abstract void to(Class<? extends T> type);
    }

    private static class RoleBinder<T> extends Binder<T> {

        private final Role<T> role;

        public RoleBinder(final Context scope, final Role<T> role) {
            super(scope);
            this.role = role;
        }

        public void to(final Supplier<? extends T> supplier) {
            context.addSupplier(role, supplier);
        }

        public void to(final T bean) {
            context.addBean(role, bean);
        }

        @Override
        public void to(final Class<? extends T> type) {
            context.addType(role, type);
        }
    }

    private static class TypeBinder<T> extends Binder<T> {

        private final Class<T> type;

        public TypeBinder(final Context scope, final Class<T> type) {
            super(scope);
            this.type = type;
        }

        public void to(final Supplier<? extends T> supplier) {
            context.addSupplier(type, supplier);
        }

        public void to(final T bean) {
            context.addBean(type, bean);
        }

        @Override
        public void to(final Class<? extends T> type) {
            context.addType(this.type, type);
        }
    }
}
