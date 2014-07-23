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

import org.maxur.jj.core.domain.Inject;
import org.maxur.jj.core.domain.JustJSystemException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
abstract class BeanWrapper {

    public static BeanWrapper wrap(final Supplier<?> supplier) {
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

    public static BeanWrapper wrap(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class of been must not be null");
        }
        return new ClassBeanWrapper(clazz);
    }

    public abstract Class type();

    public final <T> T bean(final Context context) {
        final T bean = create(context);
        final Class<?> beanClass = bean.getClass();
        context.inject(bean, getInjectedFields(beanClass));
        return bean;
    }

    protected Collection<Field> getInjectedFields(Class<?> beanClass) {
        return findInjectedFields(beanClass);
    }

    protected final Collection<Field> findInjectedFields(final Class<?> beanClass) {
        return stream(beanClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    protected abstract <T> T create(Context context);

    protected abstract boolean suitableTo(final Class type);

    private static class SupplierBeanWrapper extends BeanWrapper {

        private final Supplier<?> supplier;

        public SupplierBeanWrapper(final Supplier<?> supplier) {
            this.supplier = supplier;
        }

        public Class type() {
            return Object.class;  // TODO
        }

        @Override
        @SuppressWarnings("unchecked")
        protected <T> T create(final Context context) {
            return (T) supplier.get();
        }

        @Override
        protected boolean suitableTo(final Class type) {
            return true;    // TODO
        }
    }

    private static class ObjectBeanWrapper extends BeanWrapper {

        private final Object bean;

        private final Collection<Field> fields;

        public ObjectBeanWrapper(Object bean) {
            this.bean = bean;
            fields = findInjectedFields(bean.getClass());
        }

        @Override
        protected Collection<Field> getInjectedFields(Class<?> beanClass) {
            return fields;
        }

        public Class type() {
            return bean.getClass();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected <T> T create(Context context) {
            return (T) bean;  // TODO must be catch exception
        }

        @Override
        protected boolean suitableTo(Class type) {
            //noinspection unchecked
            return type.isAssignableFrom(type());
        }
    }

    private static class ClassBeanWrapper extends BeanWrapper {

        private final Class clazz;

        private final Constructor constructor;

        private final Collection<Field> fields;

        public ClassBeanWrapper(final Class clazz) {
            super();
            this.clazz = clazz;
            fields = findInjectedFields(clazz);
            constructor = findInjectedConstructor(clazz);
        }

        private Constructor findInjectedConstructor(Class clazz) {
            //noinspection unchecked
            final List<Constructor> constructors = stream(clazz.getDeclaredConstructors())
                    .filter(c -> (
                                    stream(c.getDeclaredAnnotations())
                                            .anyMatch(a -> Inject.class.equals(a.annotationType()))
                            )
                    ).collect(toList());
            if (constructors.size() > 1) {
                throw new JustJSystemException("More than one constructor with Inject annotation");
            }
            return constructors.isEmpty() ? null : constructors.get(0);
        }

        @Override
        protected Collection<Field> getInjectedFields(Class<?> beanClass) {
            return fields;
        }

        @Override
        public Class type() {
            return clazz;
        }

        @Override
        protected <T> T create(final Context context) {
            if (constructor == null) {
                try {
                    //noinspection unchecked
                    return (T) clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new JustJSystemException("New instance error", e);
                }
            }
            constructor.setAccessible(true);
            final Class[] parameterTypes = constructor.getParameterTypes();
            final Object[] parameters = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameters[i] = context.bean(parameterTypes[i]);
            }
            try {
                //noinspection unchecked
                return (T) constructor.newInstance(parameters);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new JustJSystemException("New instance error", e);
            }
        }

        @Override
        protected boolean suitableTo(final Class type) {
            //noinspection unchecked
            return type.isAssignableFrom(type());
        }
    }
}
