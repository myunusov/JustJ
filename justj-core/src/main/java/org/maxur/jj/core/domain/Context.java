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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public class Context extends Entity {

    private final Context parent;

    private final Map<Role, BeanWrapper> roleToBean = new HashMap<>();

    private final Map<Class, BeanWrapper> typeToBean = new HashMap<>();

    public Context() {
        this.parent = null;
    }

    Context(final Context parent) {
        this.parent = parent;
    }

    public Command command(final Consumer<Context> consumer) {
        final Command command = new Command() {
            @Override
            public void execute() {
                consumer.accept(new Context(Context.this));
            }
        };
        inject(command);
        return command;
    }

    public <T> T bean(final Role role) {
        final BeanWrapper wrapper = findBeanWrapper(role);
        if (wrapper == null) {
            throw new JustJSystemException("Bean with Role '%s' is not found.\n" +
                    "It must be added to config file.", role.toString());
        }
        // TODO replace this to wrapper.injectWith(context) to cache wrap information in wrapper
        final T result = wrapper.bean();  //TODO
        if (result == null) {
            throw new JustJSystemException("Bean with Role '%s' is not created.\n" +
                    "Check it supplier.", role.toString());
        }
        inject(result);
        return result;
    }

    protected BeanWrapper findBeanWrapper(final Role role) {
        final BeanWrapper wrapper = roleToBean.get(role);
        if (wrapper == null)  {
            if (parent != null) {
                return parent.findBeanWrapper(role);
            }
        }
        return wrapper;
    }

    public <T> T bean(final Class<T> type) {
        final BeanWrapper wrapper = findBeanWrapper(type);
        if (wrapper == null) {
            throw new JustJSystemException("Bean with Type '%s' is not found.\n" +
                    "It must be added to config file.", type.getName());

        }
        // TODO replace this to wrapper.injectWith(context) to cache wrap information in wrapper
        final T result = wrapper.bean();
        if (result == null) {
            throw new JustJSystemException("Bean with Type '%s' is not created.\n" +
                    "Check it supplier.", type.getName());
        }
        inject(result);
        return result;
    }

    protected BeanWrapper findBeanWrapper(final Class type) {
        final BeanWrapper wrapper = typeToBean.get(type);
        if (wrapper == null)  {
            if (parent != null) {
                return parent.findBeanWrapper(type);
            }
        }
        return wrapper;
    }


    public <T> void inject(T bean) {
        // TODO
    }

    void put(final Role role, final Supplier<?> supplier) {
        checkDuplicate(role);
        put(role, BeanWrapper.wrap(supplier));
    }

    void put(final Role role, final Object bean) {
        checkDuplicate(role);
        put(role, BeanWrapper.wrap(bean));
    }

    private void checkDuplicate(final Role role) {
        if (findBeanWrapper(role) != null) {
            throw new JustJSystemException("Bean of '%s' role is exist already", role);
        }
    }

    private void put(final Role role, final BeanWrapper wrap) {
        if (wrap.suitableTo(role)) {
            roleToBean.put(role, wrap);
            return;
        }
        final String message = format(
                "The type '%s' is not suitable to role '%s'",
                wrap.type().getName(),
                role.toString()
        );
        throw new IllegalArgumentException(message);
    }

    void put(final Class type, final Object bean) {
        checkDuplicate(type);
        put(type, BeanWrapper.wrap(bean));
    }

    void put(final Class type, final Supplier<?> supplier) {
        checkDuplicate(type);
        put(type, BeanWrapper.wrap(supplier));
    }

    private void checkDuplicate(final Class type) {
        if (findBeanWrapper(type) != null) {
            throw new JustJSystemException("Bean of type '%s' is exist already", type.getName());
        }
    }

    private void put(final Class type, final BeanWrapper wrap) {
        if (wrap.suitableTo(type)) {
            typeToBean.put(type, wrap);
            return;
        }
        final String message = format(
                "The type '%s' is not suitable to type '%s'",
                wrap.type().getName(),
                type.getName()
        );
        throw new IllegalArgumentException(message);
    }

    private abstract static class BeanWrapper {

        private static BeanWrapper wrap(final Supplier<?> supplier) {
            if (supplier == null) {
                throw new IllegalArgumentException("Been must not be null");
            }
            return new SupplierBeanWrapper(supplier);
        }

        public static BeanWrapper wrap(final Object bean) {
            if (bean == null) {
                throw new IllegalArgumentException("Been must not be null");
            }
            return new ObjectBeanWrapper(bean);
        }

        public abstract Class type();

        public abstract <T> T bean();

        protected abstract boolean suitableTo(Role role);

        protected abstract boolean suitableTo(final Class type);
    }

    private static class SupplierBeanWrapper extends BeanWrapper {

        private final Supplier<?> supplier;

        public SupplierBeanWrapper(final Supplier<?> supplier) {
            this.supplier = supplier;
        }

        public Class type() {
           return Object.class;  // TODO
        }

        @SuppressWarnings("unchecked")
        public <T> T bean() {
            return (T) supplier.get();
        }

        protected boolean suitableTo(final Role role) {
            return true;    // TODO
        }

        @Override
        protected boolean suitableTo(Class type) {
            return true;    // TODO
        }
    }

    private static class ObjectBeanWrapper extends BeanWrapper {

        private final Object bean;

        public ObjectBeanWrapper(Object bean) {
            this.bean = bean;
        }

        public Class type() {
            return bean.getClass();
        }

        @SuppressWarnings("unchecked")
        public <T> T bean() {
            return (T) bean;  // TODO must be catch exception
        }

        protected boolean suitableTo(final Role role) {
            return role.suitableTo(type());
        }

        @Override
        protected boolean suitableTo(Class type) {
            //noinspection unchecked
            return type.isAssignableFrom(type());
        }
    }
}
