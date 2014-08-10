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
import org.maxur.jj.core.reflection.ClassDescriptor;
import org.maxur.jj.core.reflection.ConstructorDescriptor;
import org.maxur.jj.core.reflection.FieldDescriptor;
import org.maxur.jj.core.reflection.MethodDescriptor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.maxur.jj.core.reflection.ClassDescriptor.meta;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
abstract class BaseBeanWrapper<T> implements BeanWrapper<T> {

    private final List<MemberBinder> injectableFields;

    private final List<MemberBinder> injectableMethods;

    private final List<MemberBinder> injectableConstructor;

    private final ClassDescriptor<T> metaData;

    protected BaseBeanWrapper(final Class<T> clazz) {
        // XXX Flyweight with IoC
        metaData = meta(clazz);
        injectableConstructor = findInjectableConstructor();
        injectableFields = findInjectableFields();
        injectableMethods = findInjectableMethods();
    }

    @Override
    public BeanWrapper<T> checkType(final BeanReference id) {
        //noinspection unchecked
        if (!metaData.isAssignable(id.getType())) {
            throw new IllegalArgumentException(format(
                    "The type '%s' is not suitable to %s",
                    getName(),
                    id.toString()
            ));
        }
        return this;
    }

    @Override
    public String toString() {
        return format("Bean '%s'", getName());
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T bean(final Function<BeanReference, BeanWrapper> context) {

        //  1. Get Dependencies
        //  2. Create all instances (with proxy if required)
        //  3. Inject all fields
        //  4. Inject all methods

        final Optional<T> result = create(context);
        if (result.isPresent()) {
            final T bean = result.get();
            injectFields(bean, context)
                    .injectMethods(bean, context);
            return bean;
        } else {
            return null;
        }
    }

    T inject(final T bean, final Function<BeanReference, BeanWrapper> context) {
        injectFields(bean, context)
                .injectMethods(bean, context);
        return bean;
    }

    protected List<MemberBinder> findInjectableConstructor() {
        return emptyList();
    }

    protected final List<MemberBinder> findInjectableMethods() {
        ///CLOVER:OFF
        return metaData
                .methods()
                .stream()
                .filter(MethodDescriptor::isInjectable)
                .map(MemberBinder::binder)
                .collect(toList());
        ///CLOVER:ON
    }

    protected final List<MemberBinder> findInjectableFields() {
        ///CLOVER:OFF
        return metaData
                .fields()
                .stream()
                .filter(FieldDescriptor::isInjectable)
                .map(MemberBinder::binder)
                .collect(toList());
        ///CLOVER:ON
    }


    protected Optional<MemberBinder> constructor() {
        if (injectableConstructor.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(injectableConstructor.get(0)) ;
        }
    }

    BaseBeanWrapper injectFields(final Object bean, final Function<BeanReference, BeanWrapper> context) {
        if (bean == null) {
            return this;
        }
        // XXX check and field value set should be separated
        for (MemberBinder data : injectableFields) {
            data.setValue(bean, context);
        }
        return this;
    }

    BeanWrapper injectMethods(final Object bean, final Function<BeanReference, BeanWrapper> context) {
        if (bean == null) {
            return this;
        }
        for (MemberBinder data : injectableMethods) {
            // XXX check and field value set should be separated
            data.invoke(bean, context);
        }
        return this;
    }

    protected String getName() {
        return metaData.getName();
    }

    protected ClassDescriptor<T> metaData() {
        return metaData;
    }

    static class SupplierBeanWrapper<T> extends BaseBeanWrapper<T> {

        private final Supplier<T> supplier;

        public SupplierBeanWrapper(final Supplier<T> supplier, final Class<T> clazz) {
            super(clazz);
            this.supplier = supplier;
        }
        @Override
        public Optional<T> create(final Function<BeanReference, BeanWrapper> context) {
            return Optional.ofNullable(supplier.get());
        }
    }

    static class ObjectBeanWrapper<T> extends BaseBeanWrapper<T> {

        private final T bean;

        public ObjectBeanWrapper(final T bean) {
            //noinspection unchecked
            super((Class<T>) bean.getClass());
            this.bean = bean;
        }

        @Override
        public Optional<T> create(final Function<BeanReference, BeanWrapper> context) {
            return Optional.ofNullable(bean);
        }
    }

    static class ClassBeanWrapper<T> extends BaseBeanWrapper<T> {

        public ClassBeanWrapper(final Class<T> clazz) {
            super(clazz);
        }

        protected List<MemberBinder> findInjectableConstructor() {
            ///CLOVER:OFF
            final List<MemberBinder> result = metaData()
                    .constructors()
                    .stream()
                    .filter(ConstructorDescriptor::isInjectable)
                    .map(MemberBinder::binder)
                    .collect(toList());
            ///CLOVER:ON
            return checkUnique(result);
        }

        private List<MemberBinder> checkUnique(final List<MemberBinder> constructors) {
            if (constructors.size() > 1) {
                throw new JustJSystemException("Class %s has %d Injectable constructors," +
                        " but according to JSR-330 @Inject can apply to at most one constructor per class.",
                        getName(), constructors.size());
            }
            return constructors;
        }

        @Override
        /**
         * Inject is optional for public, no-argument constructors when no other constructors are present.
         * This enables injectors to invoke default constructors.
         *
         * @param context IoC context function
         * @return Instance of Bean
         */
        public Optional<T> create(final Function<BeanReference, BeanWrapper> context) {
            // XXX check and field value set should be separated
            if (constructor().isPresent()) {
                //noinspection unchecked
                return Optional.of((T) constructor().get().newInstance(context));
            } else {
                return Optional.of(metaData().newInstance());
            }
        }

    }

}
