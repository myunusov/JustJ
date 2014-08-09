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
import reflection.ClassMetaData;
import reflection.MethodMetaData;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.maxur.jj.core.context.BeanReference.referenceBy;
import static reflection.ClassMetaData.meta;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
abstract class BeanWrapper<T> {

    private final List<FieldMetaData> injectableFields;

    private final List<MethodMetaDataWrapper> injectableMethods;

    private final ClassMetaData<T> metaData;

    protected BeanWrapper(final Class<T> clazz) {
        injectableFields = findInjectableFields(clazz);
        injectableMethods = findInjectableMethods(clazz);
        metaData = meta(clazz);
    }

    public static <T> T inject(final Function<BeanReference, BeanWrapper> context, final T bean) {
        wrap(bean).injectFields(context, bean).injectMethods(context, bean);
        return bean;
    }

    public static <T> BeanWrapper wrap(final Supplier<T> supplier, final Class<T> clazz) {
        if (supplier == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new SupplierBeanWrapper<>(supplier, clazz);
    }

    public static <O> BeanWrapper wrap(final O bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new ObjectBeanWrapper<>(bean);
    }

    public static <T> BeanWrapper wrap(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class of been must not be null");
        }
        return new ClassBeanWrapper<>(clazz);
    }

    public final T bean(final Function<BeanReference, BeanWrapper>  context) {
        final T bean = create(context);
        injectFields(context, bean);
        injectMethods(context, bean);
        return bean;
    }

    private BeanWrapper injectFields(final Function<BeanReference, BeanWrapper> context, final Object bean) {
        if (bean == null) {
            return this;
        }
        for (FieldMetaData data : injectableFields) {
        // XXX check and field value set should be separated
            final Field field = data.getField();
            final BeanReference ref = data.getReference();
            final Optional annotation = field.getDeclaredAnnotation(Optional.class);
            final BeanWrapper wrapper = context.apply(ref);
            final Object injectedBean = wrapper == null ? null : wrapper.bean(context);
            if (annotation == null) {
                checkDependency(injectedBean, ref.getType());
            }
            field.setAccessible(true);
            try {
                field.set(bean, injectedBean);
            } catch (IllegalAccessException ignore) {
                throw new IllegalStateException("Unreachable operation", ignore);
            }
        }
        return this;
    }

    private BeanWrapper injectMethods(final Function<BeanReference, BeanWrapper> context, final Object bean) {
        if (bean == null) {
            return this;
        }
        for (MethodMetaDataWrapper data : injectableMethods) {
            // XXX check and field value set should be separated
            data.invoke(bean, getParameters(context, data.getReferences()));
        }
        return this;
    }

    protected <O> O checkDependency(final O bean, final Class type) {
        if (bean == null) {
            throw new JustJSystemException("Bean of type '%s' is not found.\n" +
                    "It should be added to context.", type.getName());
        }
        return bean;
    }

    protected List<MethodMetaDataWrapper> findInjectableMethods(final Class beanClass) {
        //noinspection unchecked
        final List<MethodMetaData> methods = meta(beanClass).methods();
        return methods.stream()
                .filter(m -> m.isAnnotationPresent(Inject.class))
                .map(MethodMetaDataWrapper::new)
                .collect(toList()) ;
    }

    protected final List<FieldMetaData> findInjectableFields(final Class<?> beanClass) {
        final List<Class> parents = Collections.singletonList(beanClass);    // TODO parents methods
        return  parents.stream()
                .flatMap(c -> stream(c.getDeclaredFields()))
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .map(FieldMetaData::new)
                .collect(toList());
    }



    @SuppressWarnings("unchecked")
    protected abstract T create(final Function<BeanReference, BeanWrapper> context);

    protected Object[] getParameters(final Function<BeanReference, BeanWrapper> context, final List<BeanReference> paramTypes) {
        final Object[] parameters = new Object[paramTypes.size()];
        for (int i = 0; i < parameters.length; i++) {
            final Class type = paramTypes.get(i).getType();
            final BeanWrapper wrapper = context.apply(referenceBy(type));
            final Object injectedBean = wrapper == null ? null : wrapper.bean(context);
            parameters[i] = checkDependency(injectedBean, type);
        }
        return parameters;
    }

    void checkType(final BeanReference id) {
        //noinspection unchecked
        if (!metaData.isAssignable(id.getType())) {
            throw new IllegalArgumentException(format(
                    "The type '%s' is not suitable to %s",
                    this.metaData.getName(),
                    id.toString()
            ));
        }
    }

    private static class SupplierBeanWrapper<T> extends BeanWrapper<T> {

        private final Supplier<T> supplier;
        private final Class<T> clazz;

        public SupplierBeanWrapper(final Supplier<T> supplier, final Class<T> clazz) {
            super(clazz);
            this.supplier = supplier;
            this.clazz = clazz;
        }

        public Class<T> type() {
            return clazz;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T create(final Function<BeanReference, BeanWrapper> context) {
            return supplier.get();
        }
    }

    private static class ObjectBeanWrapper<T> extends BeanWrapper<T> {

        private final T bean;

        public ObjectBeanWrapper(final T bean) {
            //noinspection unchecked
            super((Class<T>) bean.getClass());
            this.bean = bean;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T create(Function<BeanReference, BeanWrapper>  context) {
            return bean;
        }

    }

    private static class ClassBeanWrapper<T> extends BeanWrapper<T> {

        private final Class<T> clazz;

        private final Constructor<T> injectableConstructor;

        private final List<BeanReference> constructorParams;

        public ClassBeanWrapper(final Class<T> clazz) {
            super(clazz);
            this.clazz = clazz;

            injectableConstructor = findInjectableConstructor(clazz);
            constructorParams = findInjectableConstructorParams();
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
                    .map(BeanReference::referenceBy)
                    .collect(toList());
            ///CLOVER:ON
        }

        @Override
        protected T create(final Function<BeanReference, BeanWrapper> context) {
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

    }

    private class FieldMetaData {

        private final Field field;

        private final BeanReference reference;

        private FieldMetaData(final Field field) {
            this.field = field;
            this.reference = referenceBy(field.getType());
        }

        public Field getField() {
            return field;
        }

        public BeanReference getReference() {
            return reference;
        }
    }

    private class MethodMetaDataWrapper {

        private final MethodMetaData method;

        private final List<BeanReference> references;

        private MethodMetaDataWrapper(final MethodMetaData method) {
            this.method = method;
            this.references = makeParams(method.getParameterTypes());
        }

        private List<BeanReference> makeParams(Class<?>[] parameterTypes) {
            ///CLOVER:OFF
            return stream(parameterTypes)
                    .map(BeanReference::referenceBy)
                    .collect(toList());
            ///CLOVER:ON
        }

        public List<BeanReference> getReferences() {
            return references;
        }

        public void invoke(final Object bean, final Object[] parameters) {
            method.invoke(bean, parameters);
        }

    }
}
