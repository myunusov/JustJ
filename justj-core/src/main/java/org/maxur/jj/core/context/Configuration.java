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

/**
 * The Configuration Description Interface.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/11/2014</pre>
 */
public abstract class Configuration {

    private Context context;

    final void config(final Context context) {
        this.context = context;
        config();
    }

    public abstract void config();

    public Binder bind(final Role role) {
        return new Binder(context, role);
    }

    public static class Binder {

        private final Context context;

        private final Role role;

        public Binder(final Context context, final Role role) {
            this.context = context;
            this.role = role;
        }

        public void to(final Class<?> beanClass) {
            context.put(role, beanClass);
        }

        public void to(final Object bean) {
            context.put(role, bean);
        }
    }

}
