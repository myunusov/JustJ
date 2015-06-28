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
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.maxur.jj.core.reflection.ClassDescriptor.meta;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
abstract class BaseBeanReference<T> implements BeanReference<T> {

    private final List<MemberReference> injectableFields;

    private final List<MemberReference> injectableMethods;

    private final List<MemberReference> injectableConstructor;

    private final ClassDescriptor<T> metaData;

    protected BaseBeanReference(final Class<T> clazz) {
        // XXX Flyweight with IoC
        metaData = meta(clazz);
        injectableConstructor = findInjectableConstructor();
        injectableFields = findInjectableFields();
        injectableMethods = findInjectableMethods();
    }

    @Override
    public BeanReference<T> checkType(final BeanIdentifier id) {
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
    public T bean(final InnerScope scope) {
        //  1. Get Dependencies
        //  2. Create all instances (with proxy if required)
        //  3. Inject all fields
        //  4. Inject all methods
        return doInject(create(scope), scope);
    }

    @Override
    public T inject(final Optional<T> bean, final InnerScope scope) {
        return doInject(bean, scope);
    }

    private T doInject(Optional<T> bean, InnerScope scope) {
        if (!bean.isPresent()) {
            return null;
        }
        injectFields(bean.get(), scope);
        injectMethods(bean.get(), scope);
        return bean.get();
    }

    protected List<MemberReference> findInjectableConstructor() {
        return emptyList();
    }

    protected final List<MemberReference> findInjectableMethods() {
        ///CLOVER:OFF
        return metaData
                .methods()
                .stream()
                .filter(MethodDescriptor::isInjectable)
                .map(MemberReference::binder)
                .collect(toList());
        ///CLOVER:ON
    }

    protected final List<MemberReference> findInjectableFields() {
        ///CLOVER:OFF
        return metaData
                .fields()
                .stream()
                .filter(FieldDescriptor::isInjectable)
                .map(MemberReference::binder)
                .collect(toList());
        ///CLOVER:ON
    }


    protected Optional<MemberReference> constructor() {
        if (injectableConstructor.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(injectableConstructor.get(0));
        }
    }

    BaseBeanReference injectFields(final Object bean, final InnerScope scope) {
        if (bean == null) {
            return this;
        }
        // XXX check and field value set should be separated
        for (MemberReference data : injectableFields) {
            data.setValue(bean, scope);
        }
        return this;
    }

    BeanReference injectMethods(final Object bean, final InnerScope scope) {
        if (bean == null) {
            return this;
        }
        for (MemberReference data : injectableMethods) {
            // XXX check and field value set should be separated
            data.invoke(bean, scope);
        }
        return this;
    }

    protected String getName() {
        return metaData.getName();
    }

    protected ClassDescriptor<T> metaData() {
        return metaData;
    }

    static class SupplierBeanReference<T> extends BaseBeanReference<T> {

        private final Supplier<? extends T> supplier;

        public SupplierBeanReference(final Supplier<? extends T> supplier, final Class<T> clazz) {
            super(clazz);
            this.supplier = supplier;
        }

        @Override
        public Optional<T> create(final InnerScope scope) {
            return Optional.ofNullable(supplier.get());
        }
    }

    static class ObjectBeanReference<T> extends BaseBeanReference<T> {

        private final T bean;

        public ObjectBeanReference(final T bean) {
            //noinspection unchecked
            super((Class<T>) bean.getClass());
            this.bean = bean;
        }

        @Override
        public Optional<T> create(final InnerScope scope) {
            return Optional.ofNullable(bean);
        }
    }

    static class ClassBeanReference<T> extends BaseBeanReference<T> {

        public ClassBeanReference(final Class<T> clazz) {
            super(clazz);
        }

        @Override
        protected List<MemberReference> findInjectableConstructor() {
            ///CLOVER:OFF
            final List<MemberReference> result = metaData()
                    .constructors()
                    .stream()
                    .filter(ConstructorDescriptor::isInjectable)
                    .map(MemberReference::binder)
                    .collect(toList());
            ///CLOVER:ON
            return checkUnique(result);
        }

        private List<MemberReference> checkUnique(final List<MemberReference> constructors) {
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
         * @param scope IoC context function
         * @return Instance of Bean
         */
        public Optional<T> create(final InnerScope scope) {
            // XXX check and field value set should be separated
            if (constructor().isPresent()) {
                //noinspection unchecked
                return Optional.of((T) constructor().get().newInstance(scope));
            } else {
                return Optional.of(metaData().newInstance());
            }
        }

    }

}
