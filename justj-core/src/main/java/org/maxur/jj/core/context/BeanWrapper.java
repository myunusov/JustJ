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

import org.maxur.jj.core.annotation.Optional;
import org.maxur.jj.core.domain.JustJSystemException;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.maxur.jj.core.context.BeanReference.identifier;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
abstract class BeanWrapper {

    public static <T> T inject(final Context context, final T bean) {
        wrap(bean).injectFields(context, bean).injectMethods(context, bean);
        return bean;
    }

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
        final T bean = create(context);
        injectFields(context, bean);
        injectMethods(context, bean);
        return bean;
    }

    private <T> BeanWrapper injectFields(final Context context, final T bean) {
        if (bean == null) {
            return this;
        }
        final Map<Field, BeanReference> fields = getInjectableFields(bean.getClass());
        for (Map.Entry<Field, BeanReference> entry : fields.entrySet()) { // XXX check and field value set should be separated
            final Field field = entry.getKey();
            final BeanReference id = entry.getValue();
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
        return this;
    }

    private <T> BeanWrapper injectMethods(final Context context, final T bean) {
        if (bean == null) {
            return this;
        }
        final Map<Method, List<BeanReference>> methods = getInjectableMethods(bean.getClass());
        for (Map.Entry<Method, List<BeanReference>> entry : methods.entrySet()) { // XXX check and field value set should be separated
            Method method = entry.getKey();
            try {
                method.setAccessible(true);
                method.invoke(bean, getParameters(context, entry.getValue()));
            } catch (IllegalAccessException ignore) {
                assert false : "Unreachable operation";
            } catch (InvocationTargetException | IllegalArgumentException e) {
                throw new JustJSystemException(format(
                        "Error calling Injectable Method '%s.%s'",
                        type().getName(),
                        method.getName()
                ), e);
            }
        }
        return this;
    }


    protected <T> T checkDependency(final T bean, final Class type) {
        if (bean == null) {
            throw new JustJSystemException("Bean of type '%s' is not found.\n" +
                    "It should be added to context.", type.getName());
        }
        return bean;
    }

    protected Map<Method, List<BeanReference>> getInjectableMethods(Class<?> beanClass) {
        return findInjectableMethods(beanClass);
    }

    protected Map<Field, BeanReference> getInjectableFields(Class<?> beanClass) {
        return findInjectableFields(beanClass);
    }

    protected Map<Method, List<BeanReference>> findInjectableMethods(final Class beanClass) {
        return stream(beanClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Inject.class))
                .collect(toMap((Method m) -> m, this::makeParams));
    }

    private List<BeanReference> makeParams(final Method method) {
        ///CLOVER:OFF
        return stream(method.getParameterTypes())
                .map(BeanReference::identifier)
                .collect(toList());
        ///CLOVER:ON
    }

    protected final Map<Field, BeanReference> findInjectableFields(final Class<?> beanClass) {
        return stream(beanClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .collect(toMap(f -> f, f -> identifier(f.getType())));
    }

    @SuppressWarnings("unchecked")
    protected abstract <T> T create(Context context);

    protected Object[] getParameters(final Context context, final List<BeanReference> paramTypes) {
        final Object[] parameters = new Object[paramTypes.size()];
        for (int i = 0; i < parameters.length; i++) {
            final Class type = paramTypes.get(i).getType();
            parameters[i] = checkDependency(context.bean(type), type);
        }
        return parameters;
    }

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

        private final Map<Field, BeanReference> injectableFields;
        private final Map<Method, List<BeanReference>> injectableMethods;

        public ObjectBeanWrapper(final Object bean) {
            this.bean = bean;
            injectableFields = findInjectableFields(bean.getClass());
            injectableMethods = findInjectableMethods(bean.getClass());
        }

        @Override
        protected Map<Field, BeanReference> getInjectableFields(final Class<?> beanClass) {
            return injectableFields;
        }

        @Override
        protected Map<Method, List<BeanReference>> getInjectableMethods(Class<?> beanClass) {
            return injectableMethods;
        }

        @Override
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

        private final Constructor<T> injectableConstructor;

        private final List<BeanReference> constructorParams;

        private final Map<Field, BeanReference> injectableFields;

        private final Map<Method, List<BeanReference>> injectableMethods;

        public ClassBeanWrapper(final Class<T> clazz) {
            super();
            this.clazz = clazz;

            injectableConstructor = findInjectableConstructor(clazz);
            constructorParams = findInjectableConstructorParams();
            injectableFields = findInjectableFields(clazz);
            injectableMethods = findInjectableMethods(clazz);
        }

        private Constructor<T> findInjectableConstructor(Class<T> clazz) {
            //noinspection unchecked
            final Constructor<T>[] declaredConstructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
            final List<Constructor<T>> constructors = stream(declaredConstructors)
                    .filter(c -> stream(c.getDeclaredAnnotations())
                                    .anyMatch(a -> Inject.class.equals(a.annotationType()))
                    ).collect(toList());
            if (constructors.size() > 1) {
                throw new JustJSystemException("Class %s has %d Injectable constructors," +
                        " but according to JSR-330 @Inject can apply to at most one constructor per class.",
                        clazz.getName(), constructors.size());

            }
            return constructors.isEmpty() ? null : constructors.get(0);
        }

        private List<BeanReference> findInjectableConstructorParams() {
            ///CLOVER:OFF
            return injectableConstructor == null ?
                    emptyList() :
                    Arrays.stream(injectableConstructor.getParameterTypes())
                    .map(BeanReference::identifier)
                    .collect(toList());
            ///CLOVER:ON
        }

        @Override
        protected Map<Field, BeanReference> getInjectableFields(final Class<?> beanClass) {
            return injectableFields;
        }

        @Override
        protected Map<Method, List<BeanReference>> getInjectableMethods(final Class<?> beanClass) {
            return injectableMethods;
        }

        @Override
        public Class type() {
            return clazz;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T create(final Context context) {
            try {
                if (injectableConstructor == null) {
                    return clazz.newInstance();
                } else {
                    return injectableConstructor.newInstance(getParameters(context, constructorParams));
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new JustJSystemException("Error instantiating", e);
            }
        }

        @Override
        protected boolean suitableTo(final Class type) {
            //noinspection unchecked
            return type.isAssignableFrom(type());
        }
    }
}
