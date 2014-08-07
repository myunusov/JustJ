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

    public final Context applyTo(final Context context) {
        this.context = context;
        preConfig();
        config();
        postConfig();
        return context;
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

        public abstract void to(Supplier<T> supplier);

        public abstract void to(Object bean);

        public abstract void to(Class type);
    }

    private static class RoleBinder<T> extends Binder<T> {

        private final Role<T> role;

        public RoleBinder(final Context context, final Role<T> role) {
            super(context);
            this.role = role;
        }

        public void to(final Supplier<T> supplier) {
            context.put(role, supplier);
        }

        public void to(final Object bean) {
            context.put(role, bean);
        }

        @Override
        public void to(final Class type) {
            context.put(role, type);
        }
    }

    private static class TypeBinder<T> extends Binder<T> {

        private final Class<T> type;

        public TypeBinder(final Context context, final Class<T> type) {
            super(context);
            this.type = type;
        }

        public void to(final Supplier<T> supplier) {
            context.put(type, supplier);
        }

        public void to(final Object bean) {
            context.put(type, bean);
        }

        @Override
        public void to(final Class type) {
            context.put(this.type, type);
        }
    }
}
