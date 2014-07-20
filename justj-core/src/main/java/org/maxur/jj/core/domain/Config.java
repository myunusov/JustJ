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

package org.maxur.jj.core.domain;

import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public abstract class Config<Z extends Context> extends Entity{

    private Z context;

    protected abstract Z makeContext();

    public static <T extends Context> T configBy(
            final Supplier<? extends Config<T>> supplier
    ) throws JustJSystemException {
        try {
            final Config<T> config = supplier.get();
            config.context = config.makeContext();
            config.config();
            return config.context;
        } catch (RuntimeException cause) {
            throw new JustJSystemException(
                    format("Cannot create instance of Config with Supplier '%s'", supplier.toString()),
                    cause
            );
        }
    }

    public abstract void config();

    public Binder bind(final Role role) {
        return new RoleBinder(context, role);
    }

    public Binder bind(Class type) {
        return new TypeBinder(context, type);
    }

    public abstract static class Binder {

        protected final Context context;

        protected Binder(final Context context) {
            this.context = context;
        }

        public abstract void to(Supplier<?> supplier);

        public abstract void to(Object bean);
    }

    private static class RoleBinder extends Binder {

        private final Role role;

        public RoleBinder(final Context context, final Role role) {
            super(context);
            this.role = role;
        }

        public void to(final Supplier<?> supplier) {
            context.put(role, supplier);
        }

        public void to(final Object bean) {
            context.put(role, bean);
        }
    }

    private static class TypeBinder extends Binder {

        private final Class type;

        public TypeBinder(final Context context, final Class type) {
            super(context);
            this.type = type;
        }

        public void to(final Supplier<?> supplier) {
            context.put(type, supplier);
        }

        public void to(final Object bean) {
            context.put(type, bean);
        }
    }
}
