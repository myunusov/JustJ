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

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.maxur.jj.core.context.BeanIdentifier.identifier;

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

    public static <T> BeanWrapper wrap(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class of been must not be null");
        }
        return new ClassBeanWrapper<>(clazz);
    }

    public abstract Class type();

    public final <T> T bean(final Context context) {
        return injectFields(context, create(context));
    }

    public  <T> T injectFields(final Context context, final T bean) {
        if (bean == null) {
            return null;
        }
        final Map<BeanIdentifier, Field> fields = getInjectedFields(bean.getClass());
        for (BeanIdentifier id : fields.keySet()) {             // TODO check and field value set should be separated
            final Field field = fields.get(id);
            final Optional annotation = field.getDeclaredAnnotation(Optional.class);
            final Object injectedBean = context.bean(id.getType());
            if (annotation == null) {
                checkDependency(injectedBean, id.getType());
            }
            field.setAccessible(true);
            try {
                field.set(bean, injectedBean);
            } catch (IllegalAccessException ignore) {
                assert false : "Unreachable operation";
            }
        }
        return bean;
    }

    protected <T> T checkDependency(final T bean, final Class type) {
        if (bean == null) {
            throw new JustJSystemException("Bean of type '%s' is not found.\n" +
                    "It must be added to context.", type.getName());
        }
        return bean;
    }

    protected Map<BeanIdentifier, Field> getInjectedFields(Class<?> beanClass) {
        return findInjectedFields(beanClass);
    }

    protected final Map<BeanIdentifier, Field> findInjectedFields(final Class<?> beanClass) {
        return stream(beanClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .collect(toMap(f -> identifier(f.getType()), f -> f));
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

        private final Map<BeanIdentifier, Field> fields;

        public ObjectBeanWrapper(Object bean) {
            this.bean = bean;
            fields = findInjectedFields(bean.getClass());
        }

        @Override
        protected Map<BeanIdentifier, Field> getInjectedFields(Class<?> beanClass) {
            return fields;
        }

        public Class type() {
            return bean.getClass();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected <T> T create(Context context) {
            return (T) bean;
        }

        @Override
        protected boolean suitableTo(Class type) {
            //noinspection unchecked
            return type.isAssignableFrom(type());
        }
    }

    private static class ClassBeanWrapper<T> extends BeanWrapper {

        private final Class<T> clazz;

        private final Constructor<T> constructor;

        private final List<BeanIdentifier> params;

        private final Map<BeanIdentifier, Field> fields;


        public ClassBeanWrapper(final Class<T> clazz) {
            super();
            this.clazz = clazz;
            fields = findInjectedFields(clazz);
            constructor = findInjectedConstructor(clazz);
            params = findInjectedConstructorParams();
        }

        private Constructor<T> findInjectedConstructor(Class<T> clazz) {
            //noinspection unchecked
            final Constructor<T>[] declaredConstructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
            final List<Constructor<T>> constructors = stream(declaredConstructors)
                    .filter(c -> stream(c.getDeclaredAnnotations())
                                    .anyMatch(a -> Inject.class.equals(a.annotationType()))
                    ).collect(toList());
            if (constructors.size() > 1) {
                throw new JustJSystemException("More than one constructor with Inject annotation");
            }
            return constructors.isEmpty() ? null : constructors.get(0);
        }

        private List<BeanIdentifier> findInjectedConstructorParams() {
            return constructor == null ?
                    emptyList() :
                    Arrays.stream(constructor.getParameterTypes())
                    .map(BeanIdentifier::identifier)
                    .collect(toList());
        }

        @Override
        protected Map<BeanIdentifier, Field> getInjectedFields(Class<?> beanClass) {
            return fields;
        }

        @Override
        public Class type() {
            return clazz;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T create(final Context context) {
            try {
                if (constructor == null) {
                    return clazz.newInstance();
                } else {
                    return constructor.newInstance(getParameters(context));
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new JustJSystemException("Error instantiating" +
                        (e.getMessage() == null ? "" : ": " + e.getMessage()), e);
            }
        }

        private Object[] getParameters(final Context context) {
            final Object[] parameters = new Object[params.size()];
            for (int i = 0; i < parameters.length; i++) {
                final Class type = params.get(i).getType();
                parameters[i] = checkDependency(context.bean(type), type);
            }
            return parameters;
        }

        @Override
        protected boolean suitableTo(final Class type) {
            //noinspection unchecked
            return type.isAssignableFrom(type());
        }
    }
}
