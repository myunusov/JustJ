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

import static org.maxur.jj.core.context.BeanIdentifier.identifier;

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
        return new Binder<>(context, role);
    }

    public <T> Binder<T> bind(Class<T> type) {
        return new Binder<>(context, type);
    }

    public static class Binder<T> {

        private final Context context;

        private final BeanIdentifier<T> identifier;

        private Binder(final Context scope, final Role<T> role) {
            this(scope, identifier(role));
        }

        private Binder(final Context scope, final Class<T> type) {
            this(scope, identifier(type));
        }


        private Binder(final Context context, final BeanIdentifier<T> identifier) {
            this.context = context;
            this.identifier = identifier;
        }

        public void to(final Supplier<? extends T> supplier) {
            context.addSupplier(identifier, supplier);
        }

        public void to(final T bean) {
            context.addBean(identifier, bean);
        }

        public void to(final Class<? extends T> type) {
            context.addType(identifier, type);
        }
    }

}
